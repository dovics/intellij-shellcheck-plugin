package com.shellcheck.utils;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.shellcheck.ShellcheckBundle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShellcheckReplacementsAction implements IntentionAction {

    @SafeFieldForPreview
    List<ShellcheckResult.Replacement> replacements;

    public ShellcheckReplacementsAction(List<ShellcheckResult.Replacement> replacements) {
        this.replacements = replacements;
    }
    @Override
    public @IntentionName @NotNull String getText() {
        return "Auto-correct with shellcheck";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return ShellcheckBundle.message("shellcheck.name");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile) {
        for (ShellcheckResult.Replacement r : replacements) {
            if (r.line <= 0 || r.endLine <= 0 || r.column <= 0 || r.endColumn <= 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        Document document = editor.getDocument();

        for (int i = replacements.size() - 1; i >= 0; i--) {
            ShellcheckResult.Replacement r = replacements.get(i);

            int start = document.getLineStartOffset(r.line - 1) + r.column - 1;
            int end = document.getLineStartOffset(r.endLine - 1) + r.endColumn - 1;

            document.replaceString(start, end, r.replacement);
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
