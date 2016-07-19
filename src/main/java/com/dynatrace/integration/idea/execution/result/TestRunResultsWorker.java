package com.dynatrace.integration.idea.execution.result;

import com.dynatrace.diagnostics.automation.rest.sdk.TestRunsEndpoint;
import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestRun;
import com.dynatrace.diagnostics.automation.rest.sdk.exceptions.TestRunsConnectionException;
import com.dynatrace.diagnostics.automation.rest.sdk.exceptions.TestRunsResponseException;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.execution.result.ui.TestRunResultsView;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

// Fetches results from DynatraceServer and displays them in UI
public class TestRunResultsWorker implements Runnable {
    public static final Logger LOG = Logger.getLogger(TestRunResultsWorker.class.getName());
    private static final long DELAY = 2000L; // 2 seconds delay

    private final String profileName;
    private final String testRunId;
    private final long startTime;
    private final DynatraceSettingsProvider.State settings;
    private final TestRunResultsView view;
    private final int testCount;

    public TestRunResultsWorker(TestRunResultsView view, String profileName, String testRunId, DynatraceSettingsProvider.State settings, int testCount) {
        this.view = view;
        this.profileName = profileName;
        this.testRunId = testRunId;
        this.settings = settings;
        this.testCount = testCount;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        int lastFetched = 0;
        while (System.currentTimeMillis() - this.startTime < this.settings.getServer().getTimeout() * 1000L) {
            TestRunsEndpoint endpoint = new TestRunsEndpoint(this.settings.getServer());
            try {
                TestRun testRun = endpoint.getTestRun(this.profileName, this.testRunId);
                if (!testRun.isEmpty()) {
                    if (testRun.getTestResults().size() <= lastFetched) {
                        continue;
                    }
                    lastFetched = testRun.getTestResults().size();
                    this.view.setTestRun(testRun);
                    if (testRun.getTestResults().size() >= this.testCount) {
                        LOG.log(Level.INFO, Messages.getMessage("execution.result.worker.success", this.testRunId));
                        return;
                    }
                }
                Thread.sleep(DELAY);
            } catch (TestRunsResponseException | TestRunsConnectionException e) {
                LOG.log(Level.WARNING, Messages.getMessage("execution.result.worker.error", e.getLocalizedMessage()));
                break;
            } catch (InterruptedException e) {
                LOG.log(Level.WARNING, Messages.getMessage("execution.result.worker.interrupted"));
                break;
            }
        }
        this.view.setEmptyText(Messages.getMessage("execution.result.ui.errorloading"));
    }
}
