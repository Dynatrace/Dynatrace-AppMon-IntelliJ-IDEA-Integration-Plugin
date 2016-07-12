package com.dynatrace.integration.idea.execution.result.ui;

import com.dynatrace.integration.idea.Messages;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class TestRunResultsToolWindow implements ToolWindowFactory {
    private JPanel panel;
    private Tree tree;

    public TestRunResultsToolWindow() {
        this.createComponents();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory factory = ContentFactory.SERVICE.getInstance();
        toolWindow.getContentManager().addContent(factory.createContent(this.panel, "Test Results", false));
    }

    private void createComponents() {
        this.tree = new Tree();
        this.panel = JBUI.Panels.simplePanel().addToCenter(ScrollPaneFactory.createScrollPane(this.tree));
        this.tree.getEmptyText().setText(Messages.getMessage("execution.result.ui.emptytable"));
    }
}
