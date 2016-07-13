package com.dynatrace.integration.idea.execution.result;

import com.dynatrace.diagnostics.automation.rest.sdk.TestRunsEndpoint;
import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestRun;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;

import javax.xml.bind.JAXBException;
import java.io.IOException;
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

    public TestRunResultsWorker(String profileName, String testRunId, DynatraceSettingsProvider.State settings) {
        this.profileName = profileName;
        this.testRunId = testRunId;
        this.settings = settings;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (System.currentTimeMillis() - this.startTime < this.settings.getServer().getTimeout() * 1000L) {
            TestRunsEndpoint endpoint = new TestRunsEndpoint(this.settings.getServer());
            try {
                TestRun testRun = endpoint.getTestRun(this.profileName, this.testRunId);
                if (!testRun.isEmpty()) {
                    //show results
                    return;
                }
                Thread.sleep(DELAY);
            } catch (IOException | JAXBException e) {
                LOG.log(Level.WARNING, Messages.getMessage("execution.result.worker.error", e.getLocalizedMessage()));
            } catch (InterruptedException e) {
                LOG.log(Level.WARNING, Messages.getMessage("execution.result.worker.interrupted"));
            }
        }
    }
}
