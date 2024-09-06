package com.shellcheck.settings;

import com.shellcheck.ShellcheckBundle;
import com.shellcheck.utils.NotificationService;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.PersistentStateComponent;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@State(name = "ShellcheckProjectComponent", storages = {@Storage("shellcheckPlugin.xml") })
public class Settings implements PersistentStateComponent<Settings.State> {
    private final Project project;

    Settings(Project p) {
        project = p;
    }

    public static class State {
        @NonNls
        public String shellcheckExecutable = "";
        public boolean treatAllIssuesAsWarnings;
        public boolean highlightWholeLine;
        public boolean pluginEnabled;
    }

    private State state = new State();

    Settings getInstance() {
        return project.getService(Settings.class);
    }

    @NotNull
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State s) {
        state = s;
    }

    public boolean isValid() {
        if (ShellcheckFinder.validatePath(project, state.shellcheckExecutable)) {
            return true;
        }

        validationFailed(ShellcheckBundle.message("shellcheck.settings.invalid"));
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return state.treatAllIssuesAsWarnings == settings.state.treatAllIssuesAsWarnings &&
                state.highlightWholeLine == settings.state.highlightWholeLine &&
                state.pluginEnabled == settings.state.pluginEnabled &&
                Objects.equals(state.shellcheckExecutable, settings.state.shellcheckExecutable);
    }

    public boolean isEnabled() {
        return state.pluginEnabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state.shellcheckExecutable, state.treatAllIssuesAsWarnings, state.highlightWholeLine, state.pluginEnabled);
    }

    private void validationFailed(String msg) {
        NotificationListener notificationListener = (notification, event) -> new ShellcheckSettingsConfigurable(project).showSettings();
        String errorMessage = msg + ShellcheckBundle.message("shellcheck.settings.fix");
        NotificationService notificationService = project.getService(NotificationService.class);
        notificationService.showInfoNotification(errorMessage, NotificationType.WARNING, notificationListener);
    }
}
