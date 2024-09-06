package com.shellcheck.settings;


import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;

import com.shellcheck.ShellcheckBundle;
public class ShellcheckSettingsComponent {

    private final JPanel panel;
    private final JBCheckBox pluginEnabledCheckbox = new JBCheckBox(ShellcheckBundle.message("shellcheck.settings.config.enable"));
    private final JBCheckBox treatAllIssuesAsWarningsCheckBox = new JBCheckBox(ShellcheckBundle.message("shellcheck.settings.treat.all.as.warnings"));
    private final JBCheckBox highlightWholeLineCheckBox = new JBCheckBox(ShellcheckBundle.message("shellcheck.settings.highlight.whole.line"));
    private final TextFieldWithHistoryWithBrowseButton shellcheckExeField = new TextFieldWithHistoryWithBrowseButton();

    public ShellcheckSettingsComponent() {
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel(ShellcheckBundle.message("shellcheck.settings.config.exe")), shellcheckExeField, 1, false)
                .addComponent(pluginEnabledCheckbox, 2)
                .addComponent(treatAllIssuesAsWarningsCheckBox, 2)
                .addComponent(highlightWholeLineCheckBox, 2)
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
        shellcheckExeField.setText(newText);
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