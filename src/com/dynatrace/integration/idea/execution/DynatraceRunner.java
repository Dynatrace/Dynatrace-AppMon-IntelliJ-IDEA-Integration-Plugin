package com.dynatrace.integration.idea.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import org.jetbrains.annotations.NotNull;

public class DynatraceRunner extends DefaultJavaProgramRunner {
    public static final String ID = "dynatrace.runner";

    @Override
    public String getRunnerId() {
        return ID;
    }

    public boolean canRun(@NotNull final String executorId, @NotNull final RunProfile profile) {
        return executorId.equals(DynatraceExecutor.ID) &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultRunAction) &&
                profile instanceof RunConfigurationBase;
    }

    public RunnerSettings createConfigurationData(ConfigurationInfoProvider provider) {
        return new DynatraceRunnerSettings();
    }
}
