package com.shellcheck.settings;


import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.shellcheck.utils.NotificationService;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;

import com.shellcheck.ShellcheckBundle;
public class ShellcheckSettingsComponent {

    private final Project project;
    private final JPanel panel;
    private final JBCheckBox pluginEnabledCheckbox = new JBCheckBox();
    private final JBCheckBox treatAllIssuesAsWarningsCheckBox = new JBCheckBox();
    private final JBCheckBox highlightWholeLineCheckBox = new JBCheckBox();
    private final TextFieldWithHistoryWithBrowseButton shellcheckExeField = new TextFieldWithHistoryWithBrowseButton();

    private static final Logger LOG = Logger.getInstance(ShellcheckSettingsComponent.class);
    public ShellcheckSettingsComponent(Project p) {
        project = p;
        shellcheckExeField.addBrowseFolderListener("Shellcheck", "Shellcheck", project, FileChooserDescriptorFactory.createSingleFileDescriptor(), TextComponentAccessor.STRING_COMBOBOX_WHOLE_TEXT);
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel(ShellcheckBundle.message("shellcheck.settings.config.exe")), shellcheckExeField)
                .addTooltip(ShellcheckBundle.message("shellcheck.settings.config.exe.tooltip"))
                .addLabeledComponent(new JBLabel(ShellcheckBundle.message("shellcheck.settings.config.enable")),pluginEnabledCheckbox)
                .addTooltip(ShellcheckBundle.message("shellcheck.settings.config.enable.tooltip"))
                .addLabeledComponent(new JBLabel(ShellcheckBundle.message("shellcheck.settings.treat.all.as.warnings")),treatAllIssuesAsWarningsCheckBox)
                .addTooltip(ShellcheckBundle.message("shellcheck.settings.treat.all.as.warnings.tooltip"))
                .addLabeledComponent(new JBLabel(ShellcheckBundle.message("shellcheck.settings.highlight.whole.line")), highlightWholeLineCheckBox)
                .addTooltip(ShellcheckBundle.message("shellcheck.settings.highlight.whole.line.tooltip"))
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return panel;
    }

    public JComponent getPreferredFocusedComponent() {
        return shellcheckExeField;
    }

    @NotNull
    public String getShellcheckExecutable() {
        return shellcheckExeField.getText();
    }

    public void setShellcheckExecutable(@NotNull String newText) {
        shellcheckExeField.setTextAndAddToHistory(newText);
        if (!ShellcheckFinder.validatePath(project, newText) && !newText.isEmpty()) {
            NotificationService notification = project.getService(NotificationService.class);
            notification.showInfoNotification(ShellcheckBundle.message("shellcheck.settings.path.invalid"), NotificationType.ERROR);
            LOG.warn("invalid shellcheck path: " + newText);
        }
    }

    public boolean getTreatAllIssuesAsWarnings() {
        return treatAllIssuesAsWarningsCheckBox.isSelected();
    }

    public void setTreatAllIssuesAsWarnings(boolean newStatus) {
        treatAllIssuesAsWarningsCheckBox.setSelected(newStatus);
    }

    public boolean getHighlightWholeLine() {
        return highlightWholeLineCheckBox.isSelected();
    }

    public void setHighlightWholeLine(boolean newStatus) {
        highlightWholeLineCheckBox.setSelected(newStatus);
    }

    public boolean getPluginEnabled() {
        return pluginEnabledCheckbox.isSelected();
    }

    public void setPluginEnabled(boolean newStatus) {
        pluginEnabledCheckbox.setSelected(newStatus);
    }
}