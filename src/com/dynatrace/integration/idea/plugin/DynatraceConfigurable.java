package com.dynatrace.integration.idea.plugin;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.sun.org.apache.xpath.internal.operations.Number;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by Maciej.Mionskowski on 2016-07-06.
 */
public class DynatraceConfigurable implements Configurable.NoScroll, Configurable {


    private final DynatraceSettingsProvider provider;
    private final Project project;
    private DynatraceSettingsPanel panel;

    public DynatraceConfigurable(DynatraceSettingsProvider provider, Project project) {
        this.provider = provider;
        this.project = project;
    }

    @NotNull
    public String getId() {
        return "dynatrace";
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Dynatrace";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        this.panel = new DynatraceSettingsPanel();
        return this.panel.wholePanel;
    }

    @Override
    public boolean isModified() {
        DynatraceSettingsProvider.State state = this.provider.getState();

        //agent panel
        try {
            if (state.agent.agentLibrary != this.panel.agentLibrary.getText()
                    || state.agent.collectorPort != Integer.parseInt(this.panel.collectorPort.getText())
                    || state.agent.collectorHost != this.panel.collectorHost.getText()) {
                return true;
            }


            //server panel
            if (state.server.ssl != this.panel.serverSSL.isSelected()
                    || state.server.host != this.panel.clientHost.getText()
                    || state.server.login != this.panel.login.getText()
                    //TODO: validate password
                    //|| state.server.password != this.panel.password.getPassword()
                    || state.server.restPort != Integer.parseInt(this.panel.restPort.getText())
                    || state.server.timeout != Integer.parseInt(this.panel.timeout.getText())) {
                return true;
            }

            if (state.codeLink.enabled != this.panel.enableCodeLink.isSelected()
                    || state.codeLink.javaBrowsingPerspective != this.panel.javaBrowsingPerspective.isSelected()
                    || state.codeLink.ssl != this.panel.codeLinkSSL.isSelected()
                    || state.codeLink.host != this.panel.clientHost.getText()
                    || state.codeLink.port != Integer.parseInt(this.panel.clientPort.getText())) {
                return true;
            }
        } catch (NumberFormatException e) {
            return true; //will validate in apply();
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        DynatraceSettingsProvider.State state = this.provider.getState();

        //agent panel
        state.agent.agentLibrary = this.panel.agentLibrary.getText();
        try {
            int collectorPort = Integer.parseInt(this.panel.collectorPort.getText());
            if(collectorPort < 0) {
                throw new NumberFormatException();
            }
            state.agent.collectorPort = collectorPort;
        } catch(NumberFormatException e) {
            throw new ConfigurationException("Agent's collector port must be a non-negative number.");
        }
        state.agent.collectorHost = this.panel.collectorHost.getText();

        //server panel
        state.server.ssl = this.panel.serverSSL.isSelected();
        state.server.host = this.panel.clientHost.getText();
        state.server.login = this.panel.login.getText();
        //TODO: save password in PasswordSafe
        //state.server.password = this.panel.password.getPassword();
        try {
            int restPort = Integer.parseInt(this.panel.restPort.getText());
            if(restPort < 0) {
                throw new NumberFormatException();
            }
            state.server.restPort = restPort;
        } catch(NumberFormatException e) {
            throw new ConfigurationException("Servers's port must be a non-negative number.");
        }

        state.server.timeout = Integer.parseInt(this.panel.timeout.getText());

        //codelink panel
        state.codeLink.enabled = this.panel.enableCodeLink.isSelected();
        state.codeLink.javaBrowsingPerspective = this.panel.javaBrowsingPerspective.isSelected();
        state.codeLink.ssl = this.panel.codeLinkSSL.isSelected();
        state.codeLink.host = this.panel.clientHost.getText();
        try {
            int codeLinkPort = Integer.parseInt(this.panel.clientPort.getText());
            if(codeLinkPort < 0) {
                throw new NumberFormatException();
            }
            state.codeLink.port = codeLinkPort;
        } catch(NumberFormatException e) {
            throw new ConfigurationException("Codelink's port must be a non-negative number.");
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {
        this.panel = null;

    }

    public static class DynatraceSettingsPanel {
        private JTextField serverHost;
        private JTextField restPort;
        private JCheckBox serverSSL;
        private JTextField login;
        private JPasswordField password;
        private JTextField timeout;
        private JTextField agentLibrary;
        private JTextField collectorHost;
        private JTextField collectorPort;
        private JCheckBox enableCodeLink;
        private JTextField clientHost;
        private JTextField clientPort;
        private JCheckBox codeLinkSSL;
        private JCheckBox javaBrowsingPerspective;

        private JPanel wholePanel;
    }
}
