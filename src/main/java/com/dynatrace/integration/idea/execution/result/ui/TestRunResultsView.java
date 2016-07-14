package com.dynatrace.integration.idea.execution.result.ui;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestRun;
import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestStatus;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.execution.result.actions.OpenInEditorAction;
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

    private final Project project;
    private final ListTreeTableModelOnColumns model;
    private TreeTable tree;
    private SimpleTreeBuilder treeBuilder;
    private JPanel panel;

    public TestRunResultsView(Project project) {
        this.project = project;
        this.model = new ListTreeTableModelOnColumns(null, COLUMNS.toArray(new ColumnInfo[COLUMNS.size()]));
        this.tree = new TreeTable(model);

        this.tree.getColumnModel().getColumn(0).setMinWidth(TestMeasureColumnInfo.MeasureProperty.GROUP.width * 2);
        for (TestMeasureColumnInfo.MeasureProperty prop : TestMeasureColumnInfo.MeasureProperty.values()) {
            //first column is name column which is not included in MeasureProperties, that's why we add 1.
            this.tree.getColumnModel().getColumn(prop.ordinal() + 1).setMinWidth(prop.width);
        }

        this.panel = JBUI.Panels.simplePanel().addToCenter(ScrollPaneFactory.createScrollPane(this.tree));
        this.tree.getEmptyText().setText(Messages.getMessage("execution.result.ui.loading"));
        DefaultActionGroup dag = new DefaultActionGroup();
        dag.add(new OpenInEditorAction(() -> {
            TestResultNode node = this.getSelectedNode();
            if (node != null) {
                return node.getResult();
            }
            return null;
        }, project));
        ActionManager manager = ActionManager.getInstance();
        PopupHandler.installUnknownPopupHandler(this.tree, dag, ActionManager.getInstance());
    }

    private TestResultNode getResultNodeForPath(TreePath path) {
        TreePath aPath = path;
        while (aPath != null && !(aPath.getLastPathComponent() instanceof DefaultMutableTreeNode) && !(((DefaultMutableTreeNode) aPath.getLastPathComponent()).getUserObject() instanceof TestResultNode)) {
            aPath = aPath.getParentPath();
        }
        if (aPath != null && aPath.getLastPathComponent() instanceof DefaultMutableTreeNode) {
            if (((DefaultMutableTreeNode) aPath.getLastPathComponent()).getUserObject() instanceof TestResultNode) {
                return (TestResultNode) ((DefaultMutableTreeNode) aPath.getLastPathComponent()).getUserObject();
            }
        }
        return null;
    }

    private TestResultNode getSelectedNode() {
        final TreePath[] selectionPaths = this.tree.getTree().getSelectionPaths();
        if (selectionPaths.length != 1) {
            return null;
        }
        return this.getResultNodeForPath(selectionPaths[0]);
    }

    public void setTestRun(TestRun testRun) {
        //setup presentation
        TestRunNode node = new TestRunNode(testRun);
        this.model.setRoot(new DefaultMutableTreeNode(node));
        final SimpleTreeStructure treeStructure = new SimpleTreeStructure.Impl(node);
        this.treeBuilder = new SimpleTreeBuilder(this.tree.getTree(), this.model, treeStructure, null);
        Disposer.register(this, this.treeBuilder);

        this.treeBuilder.expand(treeStructure.getRootElement(), () -> {
            this.expandFailing(treeStructure, treeStructure.getRootElement());
        });

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

    @Override
    public void dispose() {
    }
}
