package com.dynatrace.integration.idea.plugin.settings;

import com.dynatrace.integration.idea.Messages;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;

public class DynatraceSettingsConfigurable implements Configurable.NoScroll, Configurable {
    private final DynatraceSettingsProvider provider;
    private DynatraceSettingsPanel panel;

    public DynatraceSettingsConfigurable(DynatraceSettingsProvider provider) {
        this.provider = provider;
    }

    @NotNull
    public String getId() {
        return "dynatrace";
    }

    @Nls
    @Override
    public String getDisplayName() {
        return Messages.getMessage("plugin.settings.ui.displayName");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        //We call createComponent when resetting reset() settings for convenience, that's why there's a null check.
        if (this.panel == null) {
            this.panel = new DynatraceSettingsPanel();
        }

        DynatraceSettingsProvider.State state = this.provider.getState();

        //server
        this.panel.serverHost.setText(state.getServer().getHost());
        this.panel.serverSSL.setSelected(state.getServer().isSSL());
        this.panel.restPort.setText(String.valueOf(state.getServer().getPort()));
        this.panel.serverSSL.setSelected(state.getServer().isSSL());
        this.panel.login.setText(state.getServer().getLogin());
        this.panel.password.setText(state.getServer().getPassword());

        this.panel.timeout.setText(String.valueOf(state.getServer().getTimeout()));

        //agent
        this.panel.agentLibrary.setText(state.getAgent().getAgentLibrary());

        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        descriptor.setTitle(Messages.getMessage("plugin.settings.ui.choose.agent"));
        descriptor.withFileFilter((filter) -> filter == null || filter.isDirectory() || (filter.getExtension() != null && (filter.getExtension().equals("dll") || filter.getExtension().equals("so") || filter.getExtension().equals("dylib"))));

        this.panel.agentLibrary.addBrowseFolderListener(new TextBrowseFolderListener(descriptor));
        //this.panel.agentLibrary.
        this.panel.collectorHost.setText(state.getAgent().getCollectorHost());
        this.panel.collectorPort.setText(String.valueOf(state.getAgent().getCollectorPort()));

        //CodeLink
        this.panel.enableCodeLink.setSelected(state.getCodeLink().isEnabled());
        this.panel.clientHost.setText(state.getCodeLink().getHost());
        this.panel.clientPort.setText(String.valueOf(state.getCodeLink().getPort()));
        this.panel.codeLinkSSL.setSelected(state.getCodeLink().isSSL());
        //this.panel.javaBrowsingPerspective.setSelected(state.getCodeLink().javaBrowsingPerspective);

        this.panel.helpText.setContentType("text/html");
        this.panel.helpText.setEditable(false);
        this.panel.helpText.setOpaque(false);
        this.panel.helpText.addHyperlinkListener(hle -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(hle.getURL().toURI());
                } catch (Exception ex) {
                }
            }
        });
        this.panel.helpText.setText(Messages.getMessage("plugin.settings.ui.help",this.panel.helpText.getFont().getFamily()));
        return this.panel.wholePanel;
    }

    @Override
    public boolean isModified() {
        DynatraceSettingsProvider.State state = this.provider.getState();

        //agent panel
        try {
            if (!state.getAgent().getAgentLibrary().equals(this.panel.agentLibrary.getText())
                    || state.getAgent().getCollectorPort() != Integer.parseInt(this.panel.collectorPort.getText())
                    || !state.getAgent().getCollectorHost().equals(this.panel.collectorHost.getText())) {
                return true;
            }

            //server panel
            if (state.getServer().isSSL() != this.panel.serverSSL.isSelected()
                    || !state.getServer().getHost().equals(this.panel.serverHost.getText())
                    || !state.getServer().getPassword().equals(String.valueOf(this.panel.password.getPassword()))
                    || !state.getServer().getLogin().equals(this.panel.login.getText())
                    || state.getServer().getPort() != Integer.parseInt(this.panel.restPort.getText())
                    || state.getServer().getTimeout() != Integer.parseInt(this.panel.timeout.getText())) {
                return true;
            }

            if (state.getCodeLink().isEnabled() != this.panel.enableCodeLink.isSelected()
                    //|| state.getCodeLink().javaBrowsingPerspective != this.panel.javaBrowsingPerspective.isSelected()
                    || state.getCodeLink().isSSL() != this.panel.codeLinkSSL.isSelected()
                    || !state.getCodeLink().getHost().equals(this.panel.clientHost.getText())
                    || state.getCodeLink().getPort() != Integer.parseInt(this.panel.clientPort.getText())) {
                return true;
            }
        } catch (NumberFormatException e) {
            return true; //will be validated in apply();
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        DynatraceSettingsProvider.State state = this.provider.getState();

        //agent panel
        state.getAgent().setAgentLibrary(this.panel.agentLibrary.getText());
        try {
            int collectorPort = Integer.parseInt(this.panel.collectorPort.getText());
            if (collectorPort < 0) {
                throw new NumberFormatException();
            }
            state.getAgent().setCollectorPort(collectorPort);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalPort", "Agent"));
        }

        state.getAgent().setCollectorHost(this.panel.collectorHost.getText());

        //server panel
        state.getServer().setSSL(this.panel.serverSSL.isSelected());
        state.getServer().setHost(this.panel.serverHost.getText());
        state.getServer().setLogin(this.panel.login.getText());

        state.getServer().setPassword(String.valueOf(this.panel.password.getPassword()));

        try {
            int restPort = Integer.parseInt(this.panel.restPort.getText());
            if (restPort < 0) {
                throw new NumberFormatException();
            }
            state.getServer().setPort(restPort);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalPort", "Server"));
        }
        try {
            int timeout = Integer.parseInt(this.panel.timeout.getText());
            state.getServer().setTimeout(timeout);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalTimeout", "Server"));
        }

        //codelink panel
        state.getCodeLink().setEnabled(this.panel.enableCodeLink.isSelected());
        //state.getCodeLink().javaBrowsingPerspective = this.panel.javaBrowsingPerspective.isSelected();
        state.getCodeLink().setSSL(this.panel.codeLinkSSL.isSelected());
        state.getCodeLink().setHost(this.panel.clientHost.getText());
        try {
            int codeLinkPort = Integer.parseInt(this.panel.clientPort.getText());
            if (codeLinkPort < 0) {
                throw new NumberFormatException();
            }
            state.getCodeLink().setPort(codeLinkPort);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalPort", "CodeLink"));
        }
    }

    @Override
    //reset does a rollback to the previous configuration
    public void reset() {
        ApplicationManager.getApplication().invokeLater(this::createComponent);
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
        private TextFieldWithBrowseButton agentLibrary;
        private JTextField collectorHost;
        private JTextField collectorPort;
        private JCheckBox enableCodeLink;
        private JTextField clientHost;
        private JTextField clientPort;
        private JCheckBox codeLinkSSL;
        //private JCheckBox javaBrowsingPerspective;

        private JPanel wholePanel;
        private JEditorPane helpText;

        private void createUIComponents() {
            this.agentLibrary = new TextFieldWithBrowseButton();
        }
    }
}
