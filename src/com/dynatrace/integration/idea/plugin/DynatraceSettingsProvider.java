package com.dynatrace.integration.idea.plugin;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//                                                    StoragePathMacros.WORKSPACE is throwing exceptions
@State(name = "DynatraceSettingsProvider", storages = @Storage(StoragePathMacros.APP_CONFIG + "/dynatrace.settings.xml"))
public class DynatraceSettingsProvider implements PersistentStateComponent<DynatraceSettingsProvider.State> {
    static class State {
        //we might encapsulate fields and annotate them
        //http://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html#implementing-the-state-class
        public State() {
        }

        @NotNull
        public ServerSettings server = new ServerSettings();
        @NotNull
        public AgentSettings agent = new AgentSettings();
        @NotNull
        public CodeLinkSettings codeLink = new CodeLinkSettings();
    }

    private State state;

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
}
