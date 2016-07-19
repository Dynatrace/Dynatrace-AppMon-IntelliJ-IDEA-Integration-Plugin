/*
 *  Dynatrace IntelliJ IDEA Integration Plugin
 *  Copyright (c) 2008-2016, DYNATRACE LLC
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  * Neither the name of the dynaTrace software nor the names of its contributors
 *  may be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 *  BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 *
 */

package com.dynatrace.integration.idea.execution.configuration;

import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.plugin.codelink.IDEDescriptor;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.util.logging.Level;

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
                    IDEDescriptor.getInstance().log(Level.WARNING, "Error occured while opening hyperlink", "", ex.getMessage(), false);
                }
            }
        });
        this.helpText.setText(Messages.getMessage("plugin.settings.ui.help", this.helpText.getFont().getFamily()));
        return wholePanel;
    }
}
