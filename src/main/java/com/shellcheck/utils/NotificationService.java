package com.shellcheck.utils;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

public class NotificationService {

    private static final Logger LOG = Logger.getInstance(NotificationService.class);

    private static final String PLUGIN_NAME = "Shellcheck";

    private final Project project;
    NotificationService(Project p) {
        project=p;
    }

    public void showInfoNotification(String content, NotificationType type) {
        showInfoNotification(content, type, null);
    }

    public void showInfoNotification(String content, NotificationType type, NotificationListener notificationListener) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("Shellcheck")
                .createNotification(content, type)
                .notify(project);
        LOG.info(content);
    }
}
