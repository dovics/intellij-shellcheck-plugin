package com.shellcheck;

import com.shellcheck.settings.Settings;
import com.shellcheck.utils.NotificationService;
import com.intellij.psi.PsiFile;

public class ShellcheckAnnotationInput {
    private final Settings settings;
    private final NotificationService notification;
    private final PsiFile psiFile;
    private final String fileContent;

    ShellcheckAnnotationInput(Settings settings, NotificationService notification, PsiFile psiFile, String fileContent) {
        this.settings = settings;
        this.notification = notification;
        this.psiFile = psiFile;
        this.fileContent = fileContent;
    }

    Settings getSettings() {
        return settings;
    }

    NotificationService getNotification() { return notification; }

    String getCwd() {
        return psiFile.getProject().getBasePath();
    }

    String getFilePath() {
        return psiFile.getVirtualFile().getPath();
    }

    String getFileContent() {
        return fileContent;
    }

}
