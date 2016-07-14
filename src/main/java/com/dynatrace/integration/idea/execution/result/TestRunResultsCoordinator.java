package com.dynatrace.integration.idea.execution.result;

import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.execution.result.ui.TestRunResultsView;
import com.dynatrace.integration.idea.plugin.codelink.IDEDescriptor;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.intellij.ide.impl.ContentManagerWatcher;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

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

    public TestRunResultsCoordinator(DynatraceSettingsProvider provider, ToolWindowManager manager, Project project) {
        this.settingsProvider = provider;
        this.project = project;

        ToolWindow toolWindow = manager.registerToolWindow(TOOLWINDOW_ID, true, ToolWindowAnchor.BOTTOM, project, true);
        toolWindow.setSplitMode(true, null);
        toolWindow.setStripeTitle(Messages.getMessage("execution.result.ui.toolwindow.title"));
        toolWindow.setTitle(Messages.getMessage("execution.result.ui.toolwindow.title"));
        new ContentManagerWatcher(toolWindow, toolWindow.getContentManager());
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
        String testRunId;
        synchronized (this.testRuns) {
            testRunId = this.testRuns.remove(profileName);
        }

        if (testRunId == null) {
            IDEDescriptor.getInstance(this.project).log(Level.WARNING, "TestRun", "", Messages.getMessage("execution.result.display.notregistered", profileName), false);
            return;
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            TestRunResultsView view = new TestRunResultsView(this.project);

            ToolWindow toolWindow = ToolWindowManager.getInstance(this.project).getToolWindow(TestRunResultsCoordinator.TOOLWINDOW_ID);
            Content content = toolWindow.getContentManager().getFactory().createContent(view.getPanel(), profileName + " #" + testRunId.substring(0, 8), true);

            //dispose if the tab is closed
            Disposer.register(content, view);
            toolWindow.getContentManager().addContent(content);
            //focus
            toolWindow.getContentManager().setSelectedContent(content);
            toolWindow.activate(null, false);

            new Thread(new TestRunResultsWorker(view, profileName, testRunId, this.settingsProvider.getState()), "TestRunResultsFetchingThread").start();
        });
    }

    public void discardTestRun(String profileName) {
        synchronized (this.testRuns) {
            this.testRuns.remove(profileName);
        }
    }
}
