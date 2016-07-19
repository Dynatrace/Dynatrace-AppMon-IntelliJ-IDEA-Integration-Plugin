package com.dynatrace.integration.idea.execution.result;

import com.dynatrace.integration.idea.Icons;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.execution.result.ui.TestRunResultsView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestRunResultsCoordinator {
    public static final String TOOLWINDOW_ID = "DynatraceTestRunResults";
    public static final Logger LOG = Logger.getLogger(TestRunResultsCoordinator.class.getName());

    public static TestRunResultsCoordinator getInstance(Project project) {
        return ServiceManager.getService(project, TestRunResultsCoordinator.class);
    }

    private final DynatraceSettingsProvider settingsProvider;
    private final Project project;

    public TestRunResultsCoordinator(DynatraceSettingsProvider provider, ToolWindowManager manager, Project project) {
        this.settingsProvider = provider;
        this.project = project;

        ToolWindow toolWindow = manager.registerToolWindow(TOOLWINDOW_ID, true, ToolWindowAnchor.BOTTOM, project, true);
        toolWindow.setSplitMode(true, null);
        toolWindow.setStripeTitle(Messages.getMessage("execution.result.ui.toolwindow.title"));
        toolWindow.setTitle(Messages.getMessage("execution.result.ui.toolwindow.title"));
        toolWindow.setIcon(Icons.DYNATRACE13);
        new ContentManagerWatcher(toolWindow, toolWindow.getContentManager());
    }

    // Requests test results from the server and displays them in the UI
    public void requestTestRunResults(String profileName, final String trId, int testCount) {
        LOG.log(Level.INFO, Messages.getMessage("execution.result.display.requested", profileName));

        ApplicationManager.getApplication().invokeLater(() -> {
            TestRunResultsView view = new TestRunResultsView(this.project);

            ToolWindow toolWindow = ToolWindowManager.getInstance(this.project).getToolWindow(TestRunResultsCoordinator.TOOLWINDOW_ID);
            Calendar now = Calendar.getInstance();
            Content content = toolWindow.getContentManager().getFactory().createContent(view.getPanel(), Messages.getMessage("execution.result.ui.tab.title", new SimpleDateFormat("HH:mm:ss").format(now.getTime())), true);//profileName + " #" + trId.substring(0, 8), true);

            //dispose if the tab is closed
            Disposer.register(content, view);
            toolWindow.getContentManager().addContent(content);
            //focus
            toolWindow.getContentManager().setSelectedContent(content);
            toolWindow.activate(null, false);

            new Thread(new TestRunResultsWorker(view, profileName, trId, this.settingsProvider.getState(), testCount), "TestRunResultsFetchingThread").start();
        });
    }
}
