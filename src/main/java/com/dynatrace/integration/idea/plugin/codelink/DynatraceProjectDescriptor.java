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

package com.dynatrace.integration.idea.plugin.codelink;


import com.dynatrace.codelink.Callback;
import com.dynatrace.codelink.CodeLinkLookupResponse;
import com.dynatrace.codelink.ProjectDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynatraceProjectDescriptor implements ProjectDescriptor {
    private final Project project;

    public DynatraceProjectDescriptor(Project project) {
        this.project = project;
    }

    public static DynatraceProjectDescriptor getInstance(Project project) {
        return ServiceManager.getService(project, DynatraceProjectDescriptor.class);
    }

    @Override
    @NotNull
    public String getProjectName() {
        return this.project.getName();
    }

    @Override
    @Nullable
    public String getProjectPath() {
        return this.project.getBasePath();
    }


    private Navigatable getNavigatable(CodeLinkLookupResponse response) {
        //outer$inner$inner$inner or outer
        String[] innerClasses = response.className.split("\\$");

        PsiClass clazz = JavaPsiFacade.getInstance(this.project).findClass(innerClasses[0], GlobalSearchScope.allScope(this.project));
        if (clazz == null || !clazz.canNavigateToSource()) {
            return null;
        }

        for (int i = 1; i < innerClasses.length; i++) {
            PsiClass inner = clazz.findInnerClassByName(innerClasses[i], false);
            if (inner == null || !inner.canNavigateToSource()) {
                return clazz;
            }
            clazz = inner;
        }

        if (response.methodName == null) {
            return clazz;
        }

        PsiMethod[] methods = clazz.findMethodsByName(response.methodName, false);
        if (methods.length == 0) {
            return clazz;
        }

        PsiMethod jumpTo = methods[0];
        //if there is no method signature present and only one method with a given name is available
        //jump to the method, otherwise do nothing (we don't want to confuse the user)
        if (methods.length != 1 && response.arguments == null) {
            return clazz;
        } else if (methods.length > 1) {
            for (PsiMethod method : methods) {
                if (method.getParameterList().getParametersCount() != response.arguments.length) {
                    continue;
                }
                jumpTo = method;
                //verify params
                for (int i = 0; i < method.getParameterList().getParametersCount(); i++) {
                    if (!method.getParameterList().getParameters()[i].getType().getCanonicalText().equals(response.arguments[i])) {
                        jumpTo = null;
                        break;
                    }
                }
            }
        }

        return (jumpTo != null && jumpTo.canNavigateToSource()) ? jumpTo : clazz;
    }


    @Override
    public void jumpToClass(@NotNull CodeLinkLookupResponse response, @Nullable Callback<Boolean> cb) {
        //we need to jump on UI thread
        ApplicationManager.getApplication().invokeLater(() -> {
            Callback<Boolean> callback = cb != null ? cb : (b) -> {
            };
            Navigatable navigatable = this.getNavigatable(response);
            if (navigatable != null) {
                navigatable.navigate(true);
            }
            callback.call(navigatable != null);
        });
    }
}
