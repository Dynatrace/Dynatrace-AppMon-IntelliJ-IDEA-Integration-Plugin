package com.dynatrace.integration.idea.execution;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfile;
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
                //profile instanceof ModuleBasedConfiguration &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultRunAction) &&
                profile instanceof RunConfigurationBase;// &&
        // Extensions.findExtension(CoverageEngine.EP_NAME, JavaCoverageEngine.class).isApplicableTo((RunConfigurationBase)profile);
    }

//    @Override
//    public void patch(JavaParameters javaParameters, RunnerSettings settings, RunProfile runProfile, boolean beforeExecution) throws ExecutionException {
//        super.patch(javaParameters, settings,runProfile,beforeExecution);
//        StringBuilder agentParam = new StringBuilder("-agentpath:=");
//        agentParam.append("name=").append(settings).append(',');
//        javaParameters.getVMParametersList().add(agentParam.toString());
//    }
}
