package com.dynatrace.integration.idea.execution.result.actions;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestResult;
import com.dynatrace.diagnostics.codelink.CodeLinkLookupResponse;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.plugin.codelink.IDEDescriptor;
import com.dynatrace.integration.idea.plugin.codelink.ProjectDescriptor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class OpenInEditorAction extends AnAction {
    public interface TestResultProvider {
        TestResult getTestResult();
    }

    private final TestResultProvider provider;
    private final Project project;

    public OpenInEditorAction(@NotNull TestResultProvider provider, @NotNull Project project) {
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
        if (testName.length < 2) {
            return;
        }
        String className = result.getPackageName() + '.' + testName[0];
        ProjectDescriptor descriptor = ProjectDescriptor.getInstance(this.project);
        CodeLinkLookupResponse response = new CodeLinkLookupResponse();
        response.className = className;
        //if the test was parametrized the mathod name will contain parameters passed, we need to strip it
        response.methodName = testName[1].split("\\(")[0];
        descriptor.jumpToClass(response, null);
    }
}
