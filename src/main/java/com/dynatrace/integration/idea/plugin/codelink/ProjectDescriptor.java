package com.dynatrace.integration.idea.plugin.codelink;

import com.dynatrace.diagnostics.codelink.Callback;
import com.dynatrace.diagnostics.codelink.CodeLinkLookupResponse;
import com.dynatrace.diagnostics.codelink.IProjectDescriptor;
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

public class ProjectDescriptor implements IProjectDescriptor {
    public static ProjectDescriptor getInstance(Project project) {
        return ServiceManager.getService(project, ProjectDescriptor.class);
    }

    private final Project project;

    public ProjectDescriptor(Project project) {
        this.project = project;
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
