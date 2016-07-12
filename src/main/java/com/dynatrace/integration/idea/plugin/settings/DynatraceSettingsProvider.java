package com.dynatrace.integration.idea.plugin.settings;

import com.dynatrace.diagnostics.automation.rest.sdk.RESTEndpoint;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Property;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@State(name = "DynatraceSettingsProvider", storages = @Storage("dynatrace.settings.xml"))
public class DynatraceSettingsProvider implements PersistentStateComponent<DynatraceSettingsProvider.State> {

    public static RESTEndpoint endpointFromState(DynatraceSettingsProvider.State settings) throws PasswordSafeException {
        return new RESTEndpoint(settings.server.getLogin(), settings.server.getPassword(), (settings.server.isSSL() ? "https://" : "http://") + settings.server.getHost() + ":" + settings.server.getPort());
    }

    private State state;

    public static DynatraceSettingsProvider getInstance() {
        return ServiceManager.getService(DynatraceSettingsProvider.class);
    }

    @Override
    public DynatraceSettingsProvider.State getState() {
        if (state == null) {
            this.state = new DynatraceSettingsProvider.State();
        }
        return this.state;
    }

    @Override
    public void loadState(DynatraceSettingsProvider.State state) {
        if (state == null) {
            this.state = new DynatraceSettingsProvider.State();
        }
        this.state = state;
    }

    public static class State {
        @NotNull
        @Property
        private ServerSettings server = new ServerSettings();
        @NotNull
        @Property
        public AgentSettings agent = new AgentSettings();
        @NotNull
        @Property
        public CodeLinkSettings codeLink = new CodeLinkSettings();

        @NotNull
        public ServerSettings getServer() {
            return server;
        }

        @NotNull
        public AgentSettings getAgent() {
            return agent;
        }

        @NotNull
        public CodeLinkSettings getCodeLink() {
            return codeLink;
        }
    }
}
