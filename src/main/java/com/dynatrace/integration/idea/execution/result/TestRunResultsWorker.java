package com.dynatrace.integration.idea.execution.result;

import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;

// Fetches results from DynatraceServer and displays them in UI
public class TestRunResultsWorker implements Runnable {
    private final String profileName;
    private final String testRunId;
    private final DynatraceSettingsProvider.State settings;

    public TestRunResultsWorker(String profileName, String testRunId, DynatraceSettingsProvider.State settings) {
        this.profileName = profileName;
        this.testRunId = testRunId;
        this.settings = settings;
    }

    @Override
    public void run() {

    }
}
