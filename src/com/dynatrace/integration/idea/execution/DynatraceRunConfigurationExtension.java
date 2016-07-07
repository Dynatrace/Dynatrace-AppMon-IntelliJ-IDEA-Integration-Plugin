package com.dynatrace.integration.idea.execution;

import com.dynatrace.integration.idea.plugin.DynatraceSettingsProvider;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynatraceRunConfigurationExtension extends RunConfigurationExtension {

    private DynatraceConfigurableStorage storage;

    @Override
    public void updateJavaParameters(RunConfigurationBase configuration, JavaParameters javaParameters, RunnerSettings runnerSettings) throws ExecutionException {
        //Check if the action was ran by DynatraceExecutor
        if (!(runnerSettings instanceof DynatraceRunnerSettings)) {
            return;
        }

        DynatraceSettingsProvider.State settings = DynatraceSettingsProvider.getInstance().getState();
        StringBuilder builder = new StringBuilder("-agentpath:");
        builder.append(settings.agent.agentLibrary).append('=');
        builder.append("wait=").append(settings.server.timeout).append(',');
        builder.append("name=").append("idea").append(',');
        builder.append("server=").append(settings.agent.collectorHost).append(',');
        builder.append("port=").append(settings.agent.collectorPort);//.append(',');
        javaParameters.getVMParametersList().add(builder.toString());
    }

    @Override
    protected void readExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws InvalidDataException {
        if (this.storage == null) {
            this.storage = DynatraceConfigurableStorage.getOrCreateStorage(runConfiguration);
        }
        this.storage.readExternal(runConfiguration, element);
    }

    @Override
    protected void writeExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws WriteExternalException {
        if (this.storage == null) {
            return;
        }
        this.storage.writeExternal(runConfiguration, element);
    }

    @Nullable
    @Override
    protected String getEditorTitle() {
        return "Dynatrace";
    }

    @Override
    protected boolean isApplicableFor(@NotNull RunConfigurationBase runConfigurationBase) {
        return true;
    }

    @Nullable
    @Override
    protected SettingsEditor createEditor(@NotNull RunConfigurationBase base) {
        return new DynatraceConfigurables();
    }
}
