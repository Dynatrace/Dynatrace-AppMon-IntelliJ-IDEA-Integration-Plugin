/*
 *  Dynatrace IntelliJ IDEA Integration Plugin
 *  Copyright (c) 2008-2016, DYNATRACE LLC
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  * Neither the name of the dynaTrace software nor the names of its contributors
 *  may be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 *  BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 *
 */

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
    public static final Logger LOG = Logger.getLogger("#" + TestRunResultsCoordinator.class.getName());
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

    public static TestRunResultsCoordinator getInstance(Project project) {
        return ServiceManager.getService(project, TestRunResultsCoordinator.class);
    }

    // Requests test results from the server and displays them in the UI
    public void requestTestRunResults(String profileName, final String trId, int testCount) {
        //IDEDescriptor.getInstance().log(Level.INFO, "TestRuns", "", Messages.getMessage("execution.result.display.requested", profileName), false);
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

            new Thread(new TestRunResultsWorker(this.project, view, profileName, trId, this.settingsProvider.getState(), testCount), "TestRunResultsFetchingThread").start();
        });
    }
}
