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

package com.dynatrace.integration.idea.execution.result.actions;


import com.dynatrace.codelink.CodeLinkLookupResponse;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.plugin.codelink.DynatraceProjectDescriptor;
import com.dynatrace.sdk.server.testautomation.models.TestResult;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class OpenInEditorAction extends AnAction {
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
        if (result == null || result.getName() == null) {
            return;
        }
        String[] testName = result.getName().split("\\.");
        if (testName.length < 2) {
            return;
        }
        String className = result.getPackageName() + '.' + testName[0];
        DynatraceProjectDescriptor descriptor = DynatraceProjectDescriptor.getInstance(this.project);
        CodeLinkLookupResponse response = new CodeLinkLookupResponse();
        response.className = className;
        //if the test was parametrized the mathod name will contain parameters passed, we need to strip it
        response.methodName = testName[1].split("\\(")[0];
        descriptor.jumpToClass(response, null);
    }

    public interface TestResultProvider {
        TestResult getTestResult();
    }
}
