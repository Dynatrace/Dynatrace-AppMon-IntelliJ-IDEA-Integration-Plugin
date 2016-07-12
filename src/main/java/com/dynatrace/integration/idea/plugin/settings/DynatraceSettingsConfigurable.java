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
        this.panel.serverHost.setText(state.server.host);
        this.panel.serverSSL.setSelected(state.server.ssl);
        this.panel.restPort.setText(String.valueOf(state.server.restPort));
        this.panel.serverSSL.setSelected(state.server.ssl);
        this.panel.login.setText(state.server.login);
        this.panel.password.setText(state.server.password);

        this.panel.timeout.setText(String.valueOf(state.server.timeout));

        //agent
        this.panel.agentLibrary.setText(state.agent.agentLibrary);

        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        descriptor.setTitle(Messages.getMessage("plugin.settings.ui.choose.agent"));
        descriptor.withFileFilter((filter) -> filter == null || filter.isDirectory() || (filter.getExtension() != null && (filter.getExtension().equals("dll") || filter.getExtension().equals("so") || filter.getExtension().equals("dylib"))));

        this.panel.agentLibrary.addBrowseFolderListener(new TextBrowseFolderListener(descriptor));

        this.panel.collectorHost.setText(state.agent.collectorHost);
        this.panel.collectorPort.setText(String.valueOf(state.agent.collectorPort));

        //CodeLink
        this.panel.enableCodeLink.setSelected(state.codeLink.enabled);
        this.panel.clientHost.setText(state.codeLink.host);
        this.panel.clientPort.setText(String.valueOf(state.codeLink.port));
        this.panel.codeLinkSSL.setSelected(state.codeLink.ssl);
        //this.panel.javaBrowsingPerspective.setSelected(state.codeLink.javaBrowsingPerspective);

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
            if (!state.agent.agentLibrary.equals(this.panel.agentLibrary.getText())
                    || state.agent.collectorPort != Integer.parseInt(this.panel.collectorPort.getText())
                    || !state.agent.collectorHost.equals(this.panel.collectorHost.getText())) {
                return true;
            }

            //server panel
            if (state.server.ssl != this.panel.serverSSL.isSelected()
                    || !state.server.host.equals(this.panel.serverHost.getText())
                    || !state.server.password.equals(String.valueOf(this.panel.password.getPassword()))
                    || !state.server.login.equals(this.panel.login.getText())
                    || state.server.restPort != Integer.parseInt(this.panel.restPort.getText())
                    || state.server.timeout != Integer.parseInt(this.panel.timeout.getText())) {
                return true;
            }

            if (state.codeLink.enabled != this.panel.enableCodeLink.isSelected()
                    //|| state.codeLink.javaBrowsingPerspective != this.panel.javaBrowsingPerspective.isSelected()
                    || state.codeLink.ssl != this.panel.codeLinkSSL.isSelected()
                    || !state.codeLink.host.equals(this.panel.clientHost.getText())
                    || state.codeLink.port != Integer.parseInt(this.panel.clientPort.getText())) {
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
        state.agent.agentLibrary = this.panel.agentLibrary.getText();
        try {
            int collectorPort = Integer.parseInt(this.panel.collectorPort.getText());
            if (collectorPort < 0) {
                throw new NumberFormatException();
            }
            state.agent.collectorPort = collectorPort;
        } catch (NumberFormatException e) {
            throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalPort", "Agent"));
        }

        state.agent.collectorHost = this.panel.collectorHost.getText();

        //server panel
        state.server.ssl = this.panel.serverSSL.isSelected();
        state.server.host = this.panel.serverHost.getText();
        state.server.login = this.panel.login.getText();

        state.server.password = String.valueOf(this.panel.password.getPassword());

        try {
            int restPort = Integer.parseInt(this.panel.restPort.getText());
            if (restPort < 0) {
                throw new NumberFormatException();
            }
            state.server.restPort = restPort;
        } catch (NumberFormatException e) {
            throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalPort", "Server"));
        }
        try {
            int timeout = Integer.parseInt(this.panel.timeout.getText());
            state.server.timeout = timeout;
        } catch (NumberFormatException e) {
            throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalTimeout", "Server"));
        }

        //codelink panel
        state.codeLink.enabled = this.panel.enableCodeLink.isSelected();
        //state.codeLink.javaBrowsingPerspective = this.panel.javaBrowsingPerspective.isSelected();
        state.codeLink.ssl = this.panel.codeLinkSSL.isSelected();
        state.codeLink.host = this.panel.clientHost.getText();
        try {
            int codeLinkPort = Integer.parseInt(this.panel.clientPort.getText());
            if (codeLinkPort < 0) {
                throw new NumberFormatException();
            }
            state.codeLink.port = codeLinkPort;
        } catch (NumberFormatException e) {
            throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalPort", "CodeLink"));
        }
    }

    @Override
    //reset does a rollback to the previous configuration
    public void reset() {
        ApplicationManager.getApplication().invokeLater(() -> this.createComponent());
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
