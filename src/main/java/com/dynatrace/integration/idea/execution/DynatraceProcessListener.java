package com.dynatrace.integration.idea.execution;

import com.dynatrace.integration.idea.plugin.session.SessionStorage;
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
        SessionStorage ss = this.project.getComponent(SessionStorage.class);
        if (ss.isRecording(this.profileName)) {
            ss.stopRecording(this.profileName);
        }
    }

    @Override
    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {

    }

    @Override
    public void onTextAvailable(ProcessEvent event, Key outputType) {

    }
}
