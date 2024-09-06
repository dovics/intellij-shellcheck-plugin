package com.shellcheck.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

public class NotificationService {

    private static final Logger LOG = Logger.getInstance(NotificationService.class);

    private static final String PLUGIN_NAME = "Shellcheck";

    private Project project;
    NotificationService(Project p) {
        project=p;
    }

    public void showInfoNotification(String content, NotificationType type) {
        showInfoNotification(content, type, null);
    }

    public void showInfoNotification(String content, NotificationType type, NotificationListener notificationListener) {
        Notification notification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type, notificationListener);
        Notifications.Bus.notify(notification, project);
    }
}
