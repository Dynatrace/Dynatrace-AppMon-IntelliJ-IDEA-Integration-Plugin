package com.dynatrace.integration.idea.execution.result;

import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.intellij.ide.impl.ContentManagerWatcher;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentManager;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestRunResultsCoordinator {
    public static final String TOOLWINDOW_ID = "DynatraceTestRunResults";
    public static final Logger LOG = Logger.getLogger(TestRunResultsCoordinator.class.getName());

    public static TestRunResultsCoordinator getInstance(Project project) {
        return ServiceManager.getService(project, TestRunResultsCoordinator.class);
    }

    private final HashMap<String, String> testRuns = new HashMap<>();
    private final DynatraceSettingsProvider settingsProvider;
    private final Project project;
    private final ContentManager contentManager;

    public TestRunResultsCoordinator(DynatraceSettingsProvider provider, ToolWindowManager manager, Project project) {
        this.settingsProvider = provider;
        this.project = project;

        ToolWindow toolWindow = manager.registerToolWindow(TOOLWINDOW_ID, true, ToolWindowAnchor.BOTTOM, project, true);
        toolWindow.setSplitMode(true, null);
        toolWindow.setIcon(IconLoader.getIcon("/icons/dynatrace_13.png"));
        this.contentManager = toolWindow.getContentManager();
        new ContentManagerWatcher(toolWindow, this.contentManager);
    }

    // Store testRunId as ProcessHandler is not self-aware of java parameters
    public void registerTestRun(String profileName, String testRunId) {
        LOG.log(Level.INFO, Messages.getMessage("execution.result.registered", profileName, testRunId));
        synchronized (this.testRuns) {
            this.testRuns.put(profileName, testRunId);
        }
    }

    // Requests test results from the server and displays them in the UI
    public void requestTestRunResults(String profileName) {
        LOG.log(Level.INFO, Messages.getMessage("execution.result.display.requested", profileName));
        synchronized (this.testRuns) {
            String testRunId = this.testRuns.get(profileName);
            new Thread(new TestRunResultsWorker(this.project, profileName, testRunId, this.settingsProvider.getState()), "TestRunResultsFetchingThread").start();
            this.testRuns.remove(profileName);
        }
    }

    // If the execution fails we remove testRunId to avoid memory leak
    public void discardTestRun(String profileName) {
        LOG.log(Level.INFO, Messages.getMessage("execution.result.discarded", profileName));
        synchronized (this.testRuns) {
            this.testRuns.remove(profileName);
        }
    }
}
