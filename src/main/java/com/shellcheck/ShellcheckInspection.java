package com.shellcheck;

import com.intellij.codeInspection.BatchSuppressableTool;
import com.intellij.codeInspection.ExternalAnnotatorInspectionVisitor;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.codeInspection.ex.UnfairLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShellcheckInspection extends LocalInspectionTool implements BatchSuppressableTool, UnfairLocalInspectionTool {

    @NotNull
    public String getDisplayName() {
        return ShellcheckBundle.message("shellcheck.property.inspection.display.name");
    }

    @NotNull
    public String getShortName() {
        return ShellcheckBundle.message("shellcheck.property.inspection.short.name");
    }

    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        return ExternalAnnotatorInspectionVisitor.checkFileWithExternalAnnotator(file, manager, isOnTheFly, new ShellcheckExternalAnnotator());
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new ExternalAnnotatorInspectionVisitor(holder, new ShellcheckExternalAnnotator(), isOnTheFly);
    }

    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element) {
        return false;
    }


    @Override
    @NotNull
    public SuppressQuickFix @NotNull [] getBatchSuppressActions(@Nullable PsiElement element) {
        return new SuppressQuickFix[0];
    }
}