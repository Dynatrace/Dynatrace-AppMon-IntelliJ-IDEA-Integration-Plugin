package com.dynatrace.integration.idea.execution;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DynatraceConfigurables extends SettingsEditor<RunConfigurationBase> {
    private JTextField passwordField1;
    private JTextField textField1;
    private JFormattedTextField formattedTextField1;
    private JCheckBox recordSessionPerLaunchCheckBox;

    @Override
    protected void resetEditorFrom(RunConfigurationBase runConfigurationBase) {

    }

    @Override
    protected void applyEditorTo(RunConfigurationBase runConfigurationBase) throws ConfigurationException {

    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JPanel panel = new JPanel();
        JTextField text = new JTextField();
        text.setText("abc");
        panel.add(text);

        return panel;
    }
}
