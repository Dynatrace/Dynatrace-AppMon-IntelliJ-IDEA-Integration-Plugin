package com.dynatrace.integration.idea.execution.result.ui;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestRun;
import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestStatus;
import com.dynatrace.integration.idea.Messages;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTreeBuilder;
import com.intellij.ui.treeStructure.SimpleTreeStructure;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns;
import com.intellij.ui.treeStructure.treetable.TreeColumnInfo;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

public class TestRunResultsView {
    private final Project project;
    private TreeTable tree;
    private SimpleTreeBuilder treeBuilder;
    private JPanel panel;

    public TestRunResultsView(Project project, TestRun testRun) {
        this.project = project;

        //Setup columns
        final List<ColumnInfo> columns = new ArrayList<>();
        columns.add(new TreeColumnInfo("Name"));
        for (TestMeasureColumnInfo.MeasureProperty prop : TestMeasureColumnInfo.MeasureProperty.values()) {
            columns.add(new TestMeasureColumnInfo(prop));
        }

        //setup presentation
        TestRunNode node = new TestRunNode(testRun);
        final ListTreeTableModelOnColumns model = new ListTreeTableModelOnColumns(new DefaultMutableTreeNode(node), columns.toArray(new ColumnInfo[columns.size()]));
        this.tree = new TreeTable(model);

        final SimpleTreeStructure treeStructure = new SimpleTreeStructure.Impl(node);
        this.treeBuilder = new SimpleTreeBuilder(this.tree.getTree(), model, treeStructure, null);

        //dispose treebuilder
        //TODO: we might want to dispose this with Content, not project
        Disposer.register(this.project, this.treeBuilder);

        this.treeBuilder.expand(treeStructure.getRootElement(), null);
        this.treeBuilder.initRoot();

        //expand failed tests
        if (node.getStatus() != TestStatus.PASSED) {
            this.treeBuilder.expand(node, null);
            for (SimpleNode child : node.getChildren()) {
                if (child instanceof StatusProvider) {
                    if (((StatusProvider) child).getStatus() != TestStatus.PASSED) {
                        this.treeBuilder.expand(child, null);
                    }
                }
            }
        }
        this.treeBuilder.queueUpdateFrom(node, false, true);

        //set column widths
        this.tree.getColumnModel().getColumn(0).setMinWidth(TestMeasureColumnInfo.MeasureProperty.GROUP.width * 2);
        for (TestMeasureColumnInfo.MeasureProperty prop : TestMeasureColumnInfo.MeasureProperty.values()) {
            this.tree.getColumnModel().getColumn(prop.ordinal() + 1).setMinWidth(prop.width);
        }

        this.panel = JBUI.Panels.simplePanel().addToCenter(ScrollPaneFactory.createScrollPane(this.tree));
        this.tree.getEmptyText().setText(Messages.getMessage("execution.result.ui.emptytable"));
    }


    public JPanel getPanel() {
        return this.panel;
    }

}
