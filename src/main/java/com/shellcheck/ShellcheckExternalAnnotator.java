package com.shellcheck;

import com.intellij.lang.annotation.*;
import com.shellcheck.settings.Settings;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.DocumentUtil;
import com.shellcheck.utils.NotificationService;
import com.shellcheck.utils.ShellcheckResult;
import com.shellcheck.utils.ShellcheckRunner;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class ShellcheckExternalAnnotator extends ExternalAnnotator<ShellcheckAnnotationInput, ShellcheckAnnotationResult> {

    private static final Logger LOG = Logger.getInstance(ShellcheckExternalAnnotator.class);

    @Nullable
    @Override
    public ShellcheckAnnotationInput collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
        return collectInformation(file);
    }

    @Nullable
    @Override
    public ShellcheckAnnotationInput collectInformation(@NotNull PsiFile file) {
        if (file.getContext() != null) {
            return null;
        }
        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null || !virtualFile.isInLocalFileSystem()) {
            return null;
        }
        if (file.getViewProvider() instanceof MultiplePsiFilesPerDocumentFileViewProvider) {
            return null;
        }

        Settings settings = file.getProject().getService(Settings.class);
        if (!settings.isEnabled() || !settings.isValid()  || !isShellcheckFile(file)) {
            return null;
        }

        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        boolean fileModified = fileDocumentManager.isFileModified(virtualFile);
        NotificationService notification = file.getProject().getService(NotificationService.class);
        return new ShellcheckAnnotationInput(settings, notification, file, fileModified ? file.getText() : null);
    }

    @Nullable
    @Override
    public ShellcheckAnnotationResult doAnnotate(ShellcheckAnnotationInput input) {
        Settings settings = input.getSettings();
        NotificationService notificationService = input.getNotification();
        try {
            ShellcheckResult result = ShellcheckRunner.runCheck(settings.getState().shellcheckExecutable, input.getCwd(), input.getFilePath(), input.getFileContent());

            if (StringUtils.isNotEmpty(result.getErrorOutput())) {
                notificationService.showInfoNotification(result.getErrorOutput(), NotificationType.WARNING);
                return null;
            }
            return new ShellcheckAnnotationResult(input, result);
        } catch (Exception e) {
            LOG.error("Error running Shellcheck inspection: ", e);
            notificationService.showInfoNotification("Error running Shellcheck inspection: " + e.getMessage(), NotificationType.ERROR);
        }
        return null;
    }

    @Override
    public void apply(@NotNull PsiFile file, ShellcheckAnnotationResult annotationResult, @NotNull AnnotationHolder holder) {
        if (annotationResult == null) {
            return;
        }
        Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
        if (document == null) {
            return;
        }

        Settings settings = annotationResult.getInput().getSettings();
        for (ShellcheckResult.Issue issue : annotationResult.getIssues()) {
            HighlightSeverity severity = getHighlightSeverity(issue, settings.getState().treatAllIssuesAsWarnings);
            createAnnotation(holder, document, issue, severity, settings);
        }
    }

    private static HighlightSeverity getHighlightSeverity(ShellcheckResult.Issue issue, boolean treatAsWarnings) {
        return switch (issue.level) {
            case "error" -> treatAsWarnings ? HighlightSeverity.WARNING : HighlightSeverity.ERROR;
            case "warning" -> HighlightSeverity.WARNING;
            default -> // info and style
                    HighlightSeverity.WEAK_WARNING;
        };
    }

    private void createAnnotation(@NotNull AnnotationHolder holder, @NotNull Document document, @NotNull ShellcheckResult.Issue issue,
                                        @NotNull HighlightSeverity severity,
                                        Settings settings) {
        boolean showErrorOnWholeLine = settings.getState().highlightWholeLine;
        ErrorRange errorRange = new ErrorRange(document, issue);
        if (!errorRange.isValid()) {
            LOG.debug("ErrorRange isn't validation, issue.endLine: " + issue.endLine +" , document.lineCount: " + document.getLineCount());
            return;
        }

        TextRange range;
        if (showErrorOnWholeLine) {
            int start = DocumentUtil.getFirstNonSpaceCharOffset(document, errorRange.getLineRange().getStartOffset(), errorRange.getLineRange().getEndOffset());
            range = new TextRange(start, errorRange.getLineRange().getEndOffset());
        } else {
            range = new TextRange(errorRange.getColumnRange().getStartOffset(), errorRange.getColumnRange().getEndOffset());
        }

        AnnotationBuilder annotationBuilder =  holder.newAnnotation(severity, "Shellcheck: " + issue.getFormattedMessage()).range(range);

        if (issue.fix != null && issue.fix.replacements != null) {
            annotationBuilder = annotationBuilder.withFix(new ShellcheckReplacementsAction(issue.fix.replacements));
        }

        annotationBuilder.create();
    }

    private static boolean isShellcheckFile(PsiFile file) {
        // TODO move to settings?
        List<String> acceptedExtensions = Arrays.asList("sh", "bash");
        boolean isBash = file.getFileType().getName().equals("Bash");
        String fileExtension = Optional.ofNullable(file.getVirtualFile()).map(VirtualFile::getExtension).orElse("");
        return isBash || acceptedExtensions.contains(fileExtension);
    }

    private static class ErrorRange {
        private boolean valid;
        private TextRange lineRange;
        private TextRange columnRange;

        ErrorRange(Document document, ShellcheckResult.Issue issue) {
            calculate(document, issue);
        }

        boolean isValid() {
            return valid;
        }

        TextRange getLineRange() {
            return lineRange;
        }

        TextRange getColumnRange() {
            return columnRange;
        }

        private void calculate(Document document, ShellcheckResult.Issue issue) {
            int line = issue.line - 1;
            int endLine = issue.endLine == 0 ? line : issue.endLine - 1;

            if (endLine >= 0 && endLine < document.getLineCount()) {
                if(endLine == document.getLineCount() - 1) {
                    endLine = line;
                }
                TextRange beginLineRange = TextRange.create(document.getLineStartOffset(line), document.getLineEndOffset(line));
                int lineStartOffset = appendNormalizeColumn(document, beginLineRange, issue.column - 1).orElse(beginLineRange.getStartOffset());

                int endColumn = issue.endColumn == 0 ? issue.column : issue.endColumn;
                TextRange endLineRange = TextRange.create(document.getLineStartOffset(endLine), document.getLineEndOffset(endLine));
                int endLineEndOffset = appendNormalizeColumn(document, endLineRange, endColumn - 1).orElse(endLineRange.getEndOffset());

                lineRange = TextRange.create(beginLineRange.getStartOffset(), endLineRange.getEndOffset());
                columnRange = TextRange.create(lineStartOffset, endLineEndOffset);
                valid = true;
            }
        }

        private OptionalInt appendNormalizeColumn(@NotNull Document document, TextRange lineRange, int column) {
            CharSequence text = document.getImmutableCharSequence();
            int col = 0;
            for (int i = lineRange.getStartOffset(); i < lineRange.getEndOffset(); i++) {
                char c = text.charAt(i);
                col += (c == '\t' ? 8 : 1);
                if (col > column) {
                    return OptionalInt.of(i);
                }
            }
            return OptionalInt.empty();
        }
    }
}


