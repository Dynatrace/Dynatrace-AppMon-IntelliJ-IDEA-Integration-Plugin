package com.dynatrace.integration.idea.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.*;
import com.intellij.openapi.extensions.Extensions;
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
        javaParameters.getVMParametersList().add("-agentpath:C:\\Program Files\\dynaTrace\\Dynatrace 6.3\\agent\\lib64\\dtagent.dll=name=ideagroup,server=localhost,wait=5,optionTestRunIdJava=8820282a-eded-441d-8598-71181cca7a4");
    }

    @Override
    protected void readExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws InvalidDataException {
        if(this.storage == null) {
            this.storage = DynatraceConfigurableStorage.getOrCreateStorage(runConfiguration);
        }
        this.storage.readExternal(runConfiguration, element);
    }

    @Override
    protected void writeExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws WriteExternalException {
        if(this.storage == null) {
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
        final DynatraceConfigurableStorage storage = runConfigurationBase.getUserData(DynatraceConfigurableStorage.STORAGE_KEY);
        if(storage!=null) {
            return true;
        }
        for(RunConfigurationExtension extension : Extensions.getExtensions(RunConfigurationExtension.EP_NAME)) {
            if(extension == this) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    protected SettingsEditor createEditor(@NotNull RunConfigurationBase base) {
        return null;
    }
}
