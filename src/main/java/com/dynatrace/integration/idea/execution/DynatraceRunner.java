package com.dynatrace.integration.idea.execution;

import com.intellij.execution.configurations.ConfigurationInfoProvider;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import org.jetbrains.annotations.NotNull;

public class DynatraceRunner extends DefaultJavaProgramRunner {
    public static final String ID = "dynatrace.runner";

    @Override
    @NotNull
    public String getRunnerId() {
        return ID;
    }

    public boolean canRun(@NotNull final String executorId, @NotNull final RunProfile profile) {
        return executorId.equals(DynatraceExecutor.ID) &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultRunAction) &&
                profile instanceof RunConfigurationBase;
    }

    @NotNull
    public RunnerSettings createConfigurationData(ConfigurationInfoProvider provider) {
        return new DynatraceRunnerSettings();
    }
}
