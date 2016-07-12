package com.dynatrace.integration.idea.execution;

import com.dynatrace.integration.idea.execution.result.TestRunResultsCoordinator;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;

public class DynatraceProcessListener implements ProcessListener {

    private final String profileName;
    private final Project project;

    public DynatraceProcessListener(String profileName, Project project) {
        this.profileName = profileName;
        this.project = project;
    }

    @Override
    public void startNotified(ProcessEvent event) {

    }

    @Override
    public void processTerminated(ProcessEvent event) {
        // called every time process end
        TestRunResultsCoordinator coordinator = TestRunResultsCoordinator.getInstance(this.project);

        //if exitted with error
        if (event.getExitCode() != 0) {
            coordinator.discardTestRun(this.profileName);
            return;
        }
        coordinator.showTestRunResults(this.profileName);
    }

    @Override
    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {

    }

    @Override
    public void onTextAvailable(ProcessEvent event, Key outputType) {

    }
}
