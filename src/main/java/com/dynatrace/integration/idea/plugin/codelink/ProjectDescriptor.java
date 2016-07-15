package com.dynatrace.integration.idea.plugin.codelink;

import com.dynatrace.diagnostics.codelink.Callback;
import com.dynatrace.diagnostics.codelink.IProjectDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
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
    @NotNull
    public String getProjectPath() {
        return this.project.getBasePath();
    }

    @Override
    public void jumpToClass(@NotNull String className, @Nullable String methodName, @Nullable Callback<Boolean> cb) {
        //we need to jump on UI thread
        ApplicationManager.getApplication().invokeLater(() -> {
            Callback<Boolean> callback = cb != null ? cb : (b) -> {
            };

            //outer$inner$inner$inner or outer
            String[] innerClasses = className.split("\\$");

            PsiClass clazz = JavaPsiFacade.getInstance(this.project).findClass(innerClasses[0], GlobalSearchScope.allScope(this.project));
            if (clazz == null || !clazz.canNavigateToSource()) {
                callback.call(false);
                return;
            }

            for (int i = 1; i < innerClasses.length; i++) {
                PsiClass inner = clazz.findInnerClassByName(innerClasses[i], false);
                if (inner == null || !inner.canNavigateToSource()) {
                    break;
                }
                clazz = inner;
            }

            if (methodName == null) {
                clazz.navigate(true);
                callback.call(true);
                return;
            }

            PsiMethod[] method = clazz.findMethodsByName(methodName, false);
            if (method.length == 0) {
                callback.call(false);
                return;
            }

            if (method[0].canNavigateToSource()) {
                method[0].navigate(true);
            }
            callback.call(method[0].canNavigateToSource());
        });
    }
}
