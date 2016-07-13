package com.dynatrace.integration.idea.execution.result;

import com.dynatrace.diagnostics.automation.rest.sdk.TestRunsEndpoint;
import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestRun;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.execution.result.ui.TestRunResultsView;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

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
    private final Project project;

    public TestRunResultsWorker(Project project, String profileName, String testRunId, DynatraceSettingsProvider.State settings) {
        this.project = project;
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
                    LOG.log(Level.INFO, Messages.getMessage("execution.result.worker.success", this.testRunId));
                    this.displayTestRunResults(this.profileName, testRun);
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

    public void displayTestRunResults(String profileName, TestRun testRun) {
        TestRunResultsView view = new TestRunResultsView(this.project, testRun);
        ApplicationManager.getApplication().invokeLater(()-> {
            ToolWindow toolWindow = ToolWindowManager.getInstance(this.project).getToolWindow(TestRunResultsCoordinator.TOOLWINDOW_ID);
            Content content = toolWindow.getContentManager().getFactory().createContent(view.getPanel(), profileName, true);
            toolWindow.getContentManager().addContent(content);
            toolWindow.getContentManager().setSelectedContent(content);
            toolWindow.activate(null, false);
        });
    }
}
