package com.dynatrace.integration.idea.execution.result.actions;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestResult;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.plugin.codelink.IDEDescriptor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;

public class OpenInEditorAction extends AnAction {
    public interface TestResultProvider {
        TestResult getTestResult();
    }

    private final TestResultProvider provider;
    private final Project project;

    public OpenInEditorAction(TestResultProvider provider, Project project) {
        this.provider = provider;
        this.project = project;
        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(Messages.getMessage("execution.result.actions.openineditor.text"));
        presentation.setIcon(AllIcons.Actions.EditSource);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        TestResult result = this.provider.getTestResult();
        if (result == null || result.getTestName() == null) {
            return;
        }
        String[] testName = result.getTestName().split("\\.");
        String className = result.getPackageName() + '.' + testName[0];
        IDEDescriptor descriptor = IDEDescriptor.getInstance(this.project);
        descriptor.jumpToClass(className, testName[1], null);
    }
}
