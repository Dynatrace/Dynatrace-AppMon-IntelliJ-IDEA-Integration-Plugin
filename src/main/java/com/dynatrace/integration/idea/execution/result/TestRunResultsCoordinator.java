package com.dynatrace.integration.idea.execution.result;

import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestRunResultsCoordinator {
    public static final Logger LOG = Logger.getLogger(TestRunResultsCoordinator.class.getName());

    public static TestRunResultsCoordinator getInstance(Project project) {
        return ServiceManager.getService(project, TestRunResultsCoordinator.class);
    }

    private HashMap<String, String> testRuns = new HashMap<>();
    private final DynatraceSettingsProvider settingsProvider;

    public TestRunResultsCoordinator(DynatraceSettingsProvider provider) {
        this.settingsProvider = provider;
    }

    // Store testRunId as ProcessHandler is not self-aware of java parameters
    public void registerTestRun(String profileName, String testRunId) {
        LOG.log(Level.INFO, Messages.getMessage("execution.result.registered", profileName, testRunId));
        synchronized (this.testRuns) {
            this.testRuns.put(profileName, testRunId);
        }
    }

    // Requests test results from the server and displays them in the UI
    public void showTestRunResults(String profileName) {
        LOG.log(Level.INFO, Messages.getMessage("execution.result.display.requested", profileName));
        synchronized (this.testRuns) {
            String testRunId = this.testRuns.get(profileName);
            new Thread(new TestRunResultsWorker(profileName, testRunId, this.settingsProvider.getState()), "TestRunResultsFetchingThread").start();
            this.testRuns.remove(profileName);
        }
    }

    // If the execution failed we remove testRunId to avoid memory leak
    public void discardTestRun(String profileName) {
        LOG.log(Level.INFO, Messages.getMessage("execution.result.discarded", profileName));
        synchronized (this.testRuns) {
            this.testRuns.remove(profileName);
        }
    }
}
