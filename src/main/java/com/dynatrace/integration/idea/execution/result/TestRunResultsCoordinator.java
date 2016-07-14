package com.dynatrace.integration.idea.execution.result;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestRun;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.execution.result.ui.TestRunResultsView;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.intellij.ide.impl.ContentManagerWatcher;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestRunResultsCoordinator {
    public static final String TOOLWINDOW_ID = "DynatraceTestRunResults";
    public static final Logger LOG = Logger.getLogger("#" + TestRunResultsCoordinator.class.getName());

    public static TestRunResultsCoordinator getInstance(Project project) {
        return ServiceManager.getService(project, TestRunResultsCoordinator.class);
    }

    private final HashMap<String, String> testRuns = new HashMap<>();
    private final HashMap<String, TestRunResultsView> views = new HashMap<>();
    private final DynatraceSettingsProvider settingsProvider;
    private final Project project;
    private final ContentManager contentManager;

    public TestRunResultsCoordinator(DynatraceSettingsProvider provider, ToolWindowManager manager, Project project) {
        this.settingsProvider = provider;
        this.project = project;

        ToolWindow toolWindow = manager.registerToolWindow(TOOLWINDOW_ID, true, ToolWindowAnchor.BOTTOM, project, true);
        toolWindow.setSplitMode(true, null);
        toolWindow.setStripeTitle(Messages.getMessage("execution.result.ui.toolwindow.title"));
        toolWindow.setTitle(Messages.getMessage("execution.result.ui.toolwindow.title"));
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

            ApplicationManager.getApplication().invokeLater(() -> {
                synchronized (this.views) {
                    TestRunResultsView view = new TestRunResultsView(this.project);
                    this.views.put(testRunId, view);
                    ToolWindow toolWindow = ToolWindowManager.getInstance(this.project).getToolWindow(TestRunResultsCoordinator.TOOLWINDOW_ID);
                    Content content = toolWindow.getContentManager().getFactory().createContent(view.getPanel(), profileName + " #" + testRunId.substring(0, 8), true);
                    //dispose
                    Disposer.register(content, view);
                    toolWindow.getContentManager().addContent(content);
                    toolWindow.getContentManager().setSelectedContent(content);
                    toolWindow.activate(null, false);
                }
            });

            new Thread(new TestRunResultsWorker(this, profileName, testRunId, this.settingsProvider.getState()), "TestRunResultsFetchingThread").start();
            this.testRuns.remove(profileName);
        }
    }

    public void displayTestRunResults(TestRun testRun) {
        ApplicationManager.getApplication().invokeLater(() -> {
            synchronized (this.views) {
                TestRunResultsView view = this.views.remove(testRun.getId());
                if (view != null) {
                    view.setTestRun(testRun);
                }
            }
        });
    }

    public void discardTestRunResult(String testId) {
        synchronized (this.views) {
            this.views.remove(testId);
        }
    }
}
