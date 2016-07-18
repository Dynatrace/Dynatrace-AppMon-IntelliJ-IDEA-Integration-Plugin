package com.dynatrace.integration.idea.plugin.settings;

import com.dynatrace.diagnostics.automation.rest.sdk.TestRunsEndpoint;
import com.dynatrace.diagnostics.automation.rest.sdk.exceptions.TestRunsConnectionException;
import com.dynatrace.diagnostics.automation.rest.sdk.exceptions.TestRunsResponseException;
import com.dynatrace.diagnostics.codelink.Callback;
import com.dynatrace.diagnostics.codelink.CodeLinkEndpoint;
import com.dynatrace.diagnostics.codelink.IProjectDescriptor;
import com.dynatrace.diagnostics.codelink.exceptions.CodeLinkResponseException;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.plugin.codelink.IDEDescriptor;
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
    private static final String TEST_CONNECTION_MESSAGE = Messages.getMessage("plugin.settings.ui.connection.button.message");

    private static int checkPort(String strPort, String service) throws ConfigurationException {
        try {
            int port = Integer.valueOf(strPort);
            if (port < 0 || port > 0xFFFF) {
                throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalPort", service));
            }
            return port;
        } catch(NumberFormatException e) {
            throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalPort", service));
        }
    }

    private final DynatraceSettingsProvider provider;

    private DynatraceSettingsPanel panel;

    public DynatraceSettingsConfigurable(DynatraceSettingsProvider provider) {
        this.provider = provider;
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
        if (this.panel != null) {
            return this.panel.wholePanel;
        }
        this.panel = new DynatraceSettingsPanel();

        //setup server connection test
        this.panel.testServerConnection.addActionListener((event) -> {
            this.panel.testServerConnection.setText(Messages.getMessage("plugin.settings.ui.connection.button.inprogress"));

            final ServerSettings settings = new ServerSettings();
            try {
                this.applyUIToServerSettings(settings);
            } catch (ConfigurationException e) {
                this.panel.testServerConnection.setText(TEST_CONNECTION_MESSAGE + " FAIL");
                return;
            }

            this.panel.testServerConnection.setEnabled(false);
            new Thread(() -> {
                TestRunsEndpoint endpoint = new TestRunsEndpoint(settings);
                String message = TEST_CONNECTION_MESSAGE + " OK";
                try {
                    endpoint.getTestRun("DOESNTMATTER", "DOESNTMATTER");
                } catch (TestRunsResponseException e) {
                    //that's okay, we won't get a valid XML anyway
                } catch (TestRunsConnectionException e) {
                    if (!e.getMessage().equals("Not Found")) {
                        message = TEST_CONNECTION_MESSAGE + " FAIL";
                    }
                } catch (Exception e) {
                    message = TEST_CONNECTION_MESSAGE + " FAIL";
                } finally {
                    final String mess = message;
                    SwingUtilities.invokeLater(() -> {
                        this.panel.testServerConnection.setEnabled(true);
                        this.panel.testServerConnection.setText(mess);
                    });
                }
            }, "Checking thread").start();
        });

        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        descriptor.setTitle(Messages.getMessage("plugin.settings.ui.choose.agent"));
        descriptor.withFileFilter((filter) -> filter == null || filter.isDirectory() || (filter.getExtension() != null && (filter.getExtension().equals("dll") || filter.getExtension().equals("so") || filter.getExtension().equals("dylib"))));

        this.panel.agentLibrary.addBrowseFolderListener(new TextBrowseFolderListener(descriptor));

        this.panel.testCodeLinkConnection.addActionListener((event) -> {
            this.panel.testCodeLinkConnection.setText(Messages.getMessage("plugin.settings.ui.connection.button.inprogress"));

            final CodeLinkSettings settings = new CodeLinkSettings();
            try {
                this.applyUIToCodeLinkSettings(settings);
            } catch (ConfigurationException e) {
                this.panel.testCodeLinkConnection.setText(TEST_CONNECTION_MESSAGE + " FAIL");
                return;
            }

            this.panel.testCodeLinkConnection.setEnabled(false);
            new Thread(() -> {
                CodeLinkEndpoint endpoint = new CodeLinkEndpoint(new IProjectDescriptor() {
                    @NotNull
                    @Override
                    public String getProjectName() {
                        return "";
                    }

                    @NotNull
                    @Override
                    public String getProjectPath() {
                        return "";
                    }

                    @Override
                    public void jumpToClass(@NotNull String className, @Nullable String methodName, @Nullable Callback<Boolean> cb) {

                    }
                }, IDEDescriptor.getInstance(), settings);
                String message = TEST_CONNECTION_MESSAGE + " OK";
                try {
                    endpoint.connect(-1);
                } catch (CodeLinkResponseException e) {
                    //that's okay
                } catch (Exception e) {
                    message = TEST_CONNECTION_MESSAGE + " FAIL";
                } finally {
                    final String mess = message;
                    SwingUtilities.invokeLater(() -> {
                        this.panel.testCodeLinkConnection.setEnabled(true);
                        this.panel.testCodeLinkConnection.setText(mess);
                    });
                }
            }, "CodeLink checking thread").start();
        });

        //add helptext url listener
        this.panel.helpText.addHyperlinkListener(hle -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(hle.getURL().toURI());
                } catch (Exception ex) {
                }
            }
        });
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
                    || state.getCodeLink().getPort() != Integer.parseInt(this.panel.clientPort.getText())
                    || state.getCodeLink().isLegacy() != this.panel.codeLinkLegacy.isSelected()) {
                return true;
            }
        } catch (NumberFormatException e) {
            return true; //will be validated in apply();
        }
        return false;
    }

    private void applyUIToServerSettings(ServerSettings settings) throws ConfigurationException {
        //server panel
        settings.setSSL(this.panel.serverSSL.isSelected());
        settings.setHost(this.panel.serverHost.getText());
        settings.setLogin(this.panel.login.getText());
        settings.setPassword(String.valueOf(this.panel.password.getPassword()));
        settings.setPort(checkPort(this.panel.restPort.getText(), "Server"));
        try {
            int timeout = Integer.parseInt(this.panel.timeout.getText());
            settings.setTimeout(timeout);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalTimeout", "Server"));
        }
    }

    private void applyUIToCodeLinkSettings(CodeLinkSettings settings) throws ConfigurationException {
        //codelink panel
        settings.setEnabled(this.panel.enableCodeLink.isSelected());
        //settings.javaBrowsingPerspective = this.panel.javaBrowsingPerspective.isSelected();
        settings.setSSL(this.panel.codeLinkSSL.isSelected());
        settings.setHost(this.panel.clientHost.getText());
        settings.setLegacy(this.panel.codeLinkLegacy.isSelected());
        settings.setPort(checkPort(this.panel.clientPort.getText(), "CodeLink"));
    }

    @Override
    public void apply() throws ConfigurationException {
        DynatraceSettingsProvider.State state = this.provider.getState();

        //agent panel
        state.getAgent().setAgentLibrary(this.panel.agentLibrary.getText());
        state.getAgent().setCollectorPort(checkPort(this.panel.collectorPort.getText(), "Agent"));
        state.getAgent().setCollectorHost(this.panel.collectorHost.getText());

        this.applyUIToServerSettings(state.getServer());
        this.applyUIToCodeLinkSettings(state.getCodeLink());
    }

    @Override
    //reset does a rollback to the previous configuration
    public void reset() {
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


        //this.panel.agentLibrary.
        this.panel.collectorHost.setText(state.getAgent().getCollectorHost());
        this.panel.collectorPort.setText(String.valueOf(state.getAgent().getCollectorPort()));

        //CodeLink
        this.panel.enableCodeLink.setSelected(state.getCodeLink().isEnabled());
        this.panel.clientHost.setText(state.getCodeLink().getHost());
        this.panel.clientPort.setText(String.valueOf(state.getCodeLink().getPort()));
        this.panel.codeLinkSSL.setSelected(state.getCodeLink().isSSL());
        this.panel.codeLinkLegacy.setSelected(state.getCodeLink().isLegacy());

        //this.panel.javaBrowsingPerspective.setSelected(state.getCodeLink().javaBrowsingPerspective);

        this.panel.helpText.setContentType("text/html");
        this.panel.helpText.setEditable(false);
        this.panel.helpText.setOpaque(false);
        this.panel.helpText.setText(Messages.getMessage("plugin.settings.ui.help", this.panel.helpText.getFont().getFamily()));
        //ApplicationManager.getApplication().invokeLater(this::createComponent);
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
        private JButton testServerConnection;
        private JButton testCodeLinkConnection;
        private JCheckBox codeLinkLegacy;

        private void createUIComponents() {
            this.agentLibrary = new TextFieldWithBrowseButton();
        }
    }
}
