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

package com.dynatrace.integration.idea.execution.result.ui;


import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.execution.result.actions.OpenInEditorAction;
import com.dynatrace.sdk.server.testautomation.models.TestRun;
import com.dynatrace.sdk.server.testautomation.models.TestStatus;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.SimpleTreeBuilder;
import com.intellij.ui.treeStructure.SimpleTreeStructure;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns;
import com.intellij.ui.treeStructure.treetable.TreeColumnInfo;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestRunResultsView implements Disposable {
    public static final String ACTION_SHOW_IN_EDITOR = "dynatrace.showineditor";
    private static final List<ColumnInfo> COLUMNS;

    static {
        final ArrayList<ColumnInfo> columns = new ArrayList<>();
        columns.add(new TreeColumnInfo("Name"));
        for (TestMeasureColumnInfo.MeasureProperty prop : TestMeasureColumnInfo.MeasureProperty.values()) {
            columns.add(new TestMeasureColumnInfo(prop));
        }
        COLUMNS = Collections.unmodifiableList(columns);
    }

    private final ListTreeTableModelOnColumns model;
    private TreeTable tree;
    private SimpleTreeBuilder treeBuilder;
    private JPanel panel;

    public TestRunResultsView(Project project) {
        this.model = new ListTreeTableModelOnColumns(null, COLUMNS.toArray(new ColumnInfo[COLUMNS.size()]));
        this.tree = new TreeTable(this.model);

        this.tree.getColumnModel().getColumn(0).setMinWidth(TestMeasureColumnInfo.MeasureProperty.GROUP.width * 2);
        for (TestMeasureColumnInfo.MeasureProperty prop : TestMeasureColumnInfo.MeasureProperty.values()) {
            //first column is name column which is not included in MeasureProperties, that's why we add 1.
            this.tree.getColumnModel().getColumn(prop.ordinal() + 1).setMinWidth(prop.width);
        }

        this.panel = JBUI.Panels.simplePanel().addToCenter(ScrollPaneFactory.createScrollPane(this.tree));
        //set text when no tests were fetched yet
        this.setEmptyText(Messages.getMessage("execution.result.ui.loading"));

        //add rightclick dialog
        DefaultActionGroup dag = new DefaultActionGroup();
        dag.add(new OpenInEditorAction(() -> {
            TestResultNode node = this.getSelectedNode();
            if (node != null) {
                return node.getResult();
            }
            return null;
        }, project));
        PopupHandler.installUnknownPopupHandler(this.tree, dag, ActionManager.getInstance());
    }

    private TestResultNode getResultNodeForPath(TreePath path) {
        TreePath aPath = path;
        while (aPath != null) {
            //climb up until TestResult which contains package location and class name is present
            if ((aPath.getLastPathComponent() instanceof DefaultMutableTreeNode) && (((DefaultMutableTreeNode) aPath.getLastPathComponent()).getUserObject() instanceof TestResultNode)) {
                return (TestResultNode) ((DefaultMutableTreeNode) aPath.getLastPathComponent()).getUserObject();
            }
            aPath = aPath.getParentPath();
        }
        return null;
    }

    private TestResultNode getSelectedNode() {
        final TreePath[] selectionPaths = this.tree.getTree().getSelectionPaths();
        if (selectionPaths == null || selectionPaths.length != 1) {
            return null;
        }
        return this.getResultNodeForPath(selectionPaths[0]);
    }

    /**
     * Populates table with appropriate metrics
     *
     * @param testRun - testRun to display inside the table
     */
    public void setTestRun(TestRun testRun) {
        //setup presentation
        TestRunNode node = new TestRunNode(testRun);
        this.model.setRoot(new DefaultMutableTreeNode(node));
        final SimpleTreeStructure treeStructure = new SimpleTreeStructure.Impl(node);
        this.treeBuilder = new SimpleTreeBuilder(this.tree.getTree(), this.model, treeStructure, null);
        Disposer.register(this, this.treeBuilder);

        this.treeBuilder.expand(treeStructure.getRootElement(), () -> this.expandFailing(treeStructure, treeStructure.getRootElement()));

        this.treeBuilder.initRoot();
    }

    private void expandFailing(SimpleTreeStructure treeStructure, Object rootElement) {
        for (Object obj : treeStructure.getChildElements(rootElement)) {
            if (obj instanceof StatusProvider) {
                if (((StatusProvider) obj).getStatus() != TestStatus.PASSED) {
                    this.treeBuilder.expand(obj, null);
                    this.treeBuilder.queueUpdateFrom(obj, true, true);
                }
                this.expandFailing(treeStructure, obj);
            }
        }
    }

    public JPanel getPanel() {
        return this.panel;
    }

    /**
     * Sets the text when table has no nodes to display
     *
     * @param text - String to display when empty
     */
    public void setEmptyText(String text) {
        this.tree.getEmptyText().setText(text);
    }

    @Override
    public void dispose() {
    }
}
