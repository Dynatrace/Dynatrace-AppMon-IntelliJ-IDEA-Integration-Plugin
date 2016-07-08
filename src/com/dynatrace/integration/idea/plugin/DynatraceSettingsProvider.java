package com.dynatrace.integration.idea.plugin;

import com.dynatrace.integration.appmon.server.IServerSettings;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "DynatraceSettingsProvider", storages = @Storage("appmon.settings.xml"))
public class DynatraceSettingsProvider implements PersistentStateComponent<DynatraceSettingsProvider.State> {

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
        public ServerSettings server = new ServerSettings();
        @NotNull
        public AgentSettings agent = new AgentSettings();
        @NotNull
        public CodeLinkSettings codeLink = new CodeLinkSettings();

        //we might encapsulate fields and annotate them
        //http://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html#implementing-the-state-class
        public State() {
        }
    }
}
