package com.dynatrace.integration.idea.execution.configuration;

import com.dynatrace.integration.idea.Messages;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;

public class DynatraceExtensionConfigurable extends SettingsEditor<RunConfigurationBase> {
    private JTextField systemProfile;
    private JTextField agentName;
    private JTextField additionalParameters;
    private JCheckBox recordSessionPerLaunch;
    private JPanel wholePanel;
    private JEditorPane helpText;

    @Override
    protected void resetEditorFrom(RunConfigurationBase runConfigurationBase) {
        DynatraceConfigurableStorage storage = DynatraceConfigurableStorage.getOrCreateStorage(runConfigurationBase);
        this.systemProfile.setText(storage.getSystemProfile());
        this.agentName.setText(storage.getAgentName());
        this.additionalParameters.setText(storage.getAdditionalParameters());
        this.recordSessionPerLaunch.setSelected(storage.isRecordSessionPerLaunch());
    }

    @Override
    protected void applyEditorTo(RunConfigurationBase runConfigurationBase) throws ConfigurationException {
        DynatraceConfigurableStorage storage = DynatraceConfigurableStorage.getOrCreateStorage(runConfigurationBase);
        storage.setAgentName(this.agentName.getText());
        storage.setSystemProfile(this.systemProfile.getText());
        storage.setAdditionalParameters(this.additionalParameters.getText());
        storage.setRecordSessionPerLaunch(this.recordSessionPerLaunch.isSelected());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        this.helpText.setContentType("text/html");
        this.helpText.setEditable(false);
        this.helpText.setOpaque(false);
        this.helpText.addHyperlinkListener(hle -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(hle.getURL().toURI());
                } catch (Exception ex) {
                }
            }
        });
        this.helpText.setText(Messages.getMessage("plugin.settings.ui.help",this.helpText.getFont().getFamily()));
        return wholePanel;
    }
}
