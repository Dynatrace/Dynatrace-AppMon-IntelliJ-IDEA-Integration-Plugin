package com.dynatrace.integration.idea.plugin;

import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DynatraceSettingsConfigurable implements Configurable.NoScroll, Configurable {
    public static final String PS_SERVER_PWD_ID = "serverPassword";

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
        try {
            String password = PasswordSafe.getInstance().getPassword(null, DynatraceSettingsConfigurable.class, PS_SERVER_PWD_ID);
            if (password != null) {
                this.panel.password.setText(password);
            } else {
                this.panel.password.setText(ServerSettings.DEFAULT_PASSWORD);
            }
        } catch (PasswordSafeException e) {
            e.printStackTrace();
        }
        this.panel.timeout.setText(String.valueOf(state.server.timeout));

        //agent
        this.panel.agentLibrary.setText(state.agent.agentLibrary);

        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        descriptor.setTitle("Choose agent path");
        descriptor.withFileFilter((filter) -> filter == null || filter.isDirectory() || (filter.getExtension() != null && (filter.getExtension().equals("dll") || filter.getExtension().equals("so"))));

        this.panel.agentLibrary.addBrowseFolderListener(new TextBrowseFolderListener(descriptor));

        this.panel.collectorHost.setText(state.agent.collectorHost);
        this.panel.collectorPort.setText(String.valueOf(state.agent.collectorPort));

        //CodeLink
        this.panel.enableCodeLink.setSelected(state.codeLink.enabled);
        this.panel.clientHost.setText(state.codeLink.host);
        this.panel.clientPort.setText(String.valueOf(state.codeLink.port));
        this.panel.codeLinkSSL.setSelected(state.codeLink.ssl);
        this.panel.javaBrowsingPerspective.setSelected(state.codeLink.javaBrowsingPerspective);
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

            //shame on you IntelliJ, storing passwords in string...
            String password = PasswordSafe.getInstance().getPassword(null, DynatraceSettingsConfigurable.class, PS_SERVER_PWD_ID);
            if (!String.valueOf(this.panel.password.getPassword()).equals(password)) {
                if (!(password == null && String.valueOf(this.panel.password.getPassword()).equals(ServerSettings.DEFAULT_PASSWORD))) {
                    return true;
                }
            }
            //server panel
            if (state.server.ssl != this.panel.serverSSL.isSelected()
                    || !state.server.host.equals(this.panel.serverHost.getText())
                    || !state.server.login.equals(this.panel.login.getText())
                    || state.server.restPort != Integer.parseInt(this.panel.restPort.getText())
                    || state.server.timeout != Integer.parseInt(this.panel.timeout.getText())) {
                return true;
            }

            if (state.codeLink.enabled != this.panel.enableCodeLink.isSelected()
                    || state.codeLink.javaBrowsingPerspective != this.panel.javaBrowsingPerspective.isSelected()
                    || state.codeLink.ssl != this.panel.codeLinkSSL.isSelected()
                    || !state.codeLink.host.equals(this.panel.clientHost.getText())
                    || state.codeLink.port != Integer.parseInt(this.panel.clientPort.getText())) {
                return true;
            }
        } catch (NumberFormatException e) {
            return true; //will be validated in apply();
        } catch (PasswordSafeException e) {
            e.printStackTrace();
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
            throw new ConfigurationException("Agent's collector port must be a non-negative number.");
        }

        state.agent.collectorHost = this.panel.collectorHost.getText();

        //server panel
        state.server.ssl = this.panel.serverSSL.isSelected();
        state.server.host = this.panel.clientHost.getText();
        state.server.login = this.panel.login.getText();

        try {
            String password = PasswordSafe.getInstance().getPassword(null, DynatraceSettingsConfigurable.class, PS_SERVER_PWD_ID);
            //check if passwords do not match
            if (!String.valueOf(this.panel.password.getPassword()).equals(password)) {
                //check if the password is a default password and the stored one is not the default one
                if (!(password == null && String.valueOf(this.panel.password.getPassword()).equals(ServerSettings.DEFAULT_PASSWORD))) {
                    PasswordSafe.getInstance().storePassword(null, DynatraceSettingsConfigurable.class, PS_SERVER_PWD_ID, String.valueOf(this.panel.password.getPassword()));
                }
            }
        } catch (PasswordSafeException e) {
            throw new ConfigurationException(e.getMessage());
        }

        try {
            int restPort = Integer.parseInt(this.panel.restPort.getText());
            if (restPort < 0) {
                throw new NumberFormatException();
            }
            state.server.restPort = restPort;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Servers's port must be a non-negative number.");
        }
        try {
            int timeout = Integer.parseInt(this.panel.timeout.getText());
            state.server.timeout = timeout;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Servers's timeout must be a number.");
        }

        //codelink panel
        state.codeLink.enabled = this.panel.enableCodeLink.isSelected();
        state.codeLink.javaBrowsingPerspective = this.panel.javaBrowsingPerspective.isSelected();
        state.codeLink.ssl = this.panel.codeLinkSSL.isSelected();
        state.codeLink.host = this.panel.clientHost.getText();
        try {
            int codeLinkPort = Integer.parseInt(this.panel.clientPort.getText());
            if (codeLinkPort < 0) {
                throw new NumberFormatException();
            }
            state.codeLink.port = codeLinkPort;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Codelink's port must be a non-negative number.");
        }
    }

    @Override
    //reset does a rollback to the previous configuration
    public void reset() {
        ApplicationManager.getApplication().invokeLater(()->this.createComponent());
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
        private JCheckBox javaBrowsingPerspective;

        private JPanel wholePanel;

        private void createUIComponents() {
            this.agentLibrary = new TextFieldWithBrowseButton();
        }
    }
}
