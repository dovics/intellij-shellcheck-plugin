package com.shellcheck.settings;

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ex.SingleConfigurableEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ShellcheckSettingsConfigurable implements Configurable {
    private ShellcheckSettingsComponent ShellcheckSettingsComponent;

    private final Project project;

    public ShellcheckSettingsConfigurable(Project p) {
        project=p;
    }
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "SDK: Application Settings Example";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return ShellcheckSettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        ShellcheckSettingsComponent = new ShellcheckSettingsComponent();
        return ShellcheckSettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        Settings.State state = project.getService(Settings.class).getState();

        return !ShellcheckSettingsComponent.getShellcheckExecutable().equals(state.shellcheckExecutable) ||
                ShellcheckSettingsComponent.getHighlightWholeLine() != state.highlightWholeLine ||
                ShellcheckSettingsComponent.getPluginEnabled() != state.pluginEnabled ||
                ShellcheckSettingsComponent.getTreatAllIssuesAsWarnings() != state.treatAllIssuesAsWarnings;
    }

    @Override
    public void apply() {
        Settings.State state = project.getService(Settings.class).getState();

        state.pluginEnabled = ShellcheckSettingsComponent.getPluginEnabled();
        state.treatAllIssuesAsWarnings = ShellcheckSettingsComponent.getTreatAllIssuesAsWarnings();
        state.shellcheckExecutable = ShellcheckSettingsComponent.getShellcheckExecutable();
        state.highlightWholeLine = ShellcheckSettingsComponent.getHighlightWholeLine();

    }

    @Override
    public void reset() {
        Settings.State state = project.getService(Settings.class).getState();

        ShellcheckSettingsComponent.setShellcheckExecutable(state.shellcheckExecutable);
        ShellcheckSettingsComponent.setPluginEnabled(state.pluginEnabled);
        ShellcheckSettingsComponent.setHighlightWholeLine(state.highlightWholeLine);
        ShellcheckSettingsComponent.setTreatAllIssuesAsWarnings(state.treatAllIssuesAsWarnings);
    }

    @Override
    public void disposeUIResources() {
        ShellcheckSettingsComponent = null;
    }

    public void showSettings() {
        String dimensionKey = ShowSettingsUtilImpl.createDimensionKey(this);
        SingleConfigurableEditor singleConfigurableEditor = new SingleConfigurableEditor(project, this, dimensionKey, false);
        singleConfigurableEditor.show();
    }
}
