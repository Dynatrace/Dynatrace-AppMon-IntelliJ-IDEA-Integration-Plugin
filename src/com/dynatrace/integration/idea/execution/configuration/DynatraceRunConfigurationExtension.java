package com.dynatrace.integration.idea.execution.configuration;

import com.dynatrace.integration.idea.execution.DynatraceRunnerSettings;
import com.dynatrace.integration.idea.execution.configuration.DynatraceConfigurableStorage;
import com.dynatrace.integration.idea.execution.configuration.DynatraceExtensionConfigurable;
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

    @Override
    public void updateJavaParameters(RunConfigurationBase configuration, JavaParameters javaParameters, RunnerSettings runnerSettings) throws ExecutionException {
        //Check if the action was ran by DynatraceExecutor
        if (!(runnerSettings instanceof DynatraceRunnerSettings)) {
            return;
        }

        DynatraceSettingsProvider.State settings = DynatraceSettingsProvider.getInstance().getState();
        DynatraceConfigurableStorage executionSettings = DynatraceConfigurableStorage.getOrCreateStorage(configuration);
        StringBuilder builder = new StringBuilder("-agentpath:");
        builder.append(settings.agent.agentLibrary).append('=');
        builder.append("wait=").append(settings.server.timeout).append(',');
        builder.append("name=").append(executionSettings.getAgentName()).append(',');
        builder.append("server=").append(settings.agent.collectorHost).append(',');
        builder.append("port=").append(settings.agent.collectorPort);//.append(',');
        javaParameters.getVMParametersList().add(builder.toString());
    }

    @Override
    protected void readExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws InvalidDataException {
        DynatraceConfigurableStorage.getOrCreateStorage(runConfiguration).readExternal(element);
    }

    @Override
    protected void writeExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws WriteExternalException {
        DynatraceConfigurableStorage.getOrCreateStorage(runConfiguration).writeExternal(element);
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
        return new DynatraceExtensionConfigurable();
    }
}
