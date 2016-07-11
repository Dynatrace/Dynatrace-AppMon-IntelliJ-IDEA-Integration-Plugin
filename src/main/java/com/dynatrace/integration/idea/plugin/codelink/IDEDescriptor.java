package com.dynatrace.integration.idea.plugin.codelink;

import com.dynatrace.diagnostics.codelink.Callback;
import com.dynatrace.diagnostics.codelink.IIDEDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;


public class IDEDescriptor implements IIDEDescriptor {
    public static IDEDescriptor getInstance() {
        return ServiceManager.getService(IDEDescriptor.class);
    }

    private final Project project;

    public IDEDescriptor(Project project) {
        this.project = project;
    }

    @Override
    public String getVersion() {
        return ApplicationInfo.getInstance().getFullVersion();
    }

    @Override
    public String getProjectName() {
        return this.project.getName();
    }

    @Override
    public String getProjectPath() {
        return this.project.getBasePath();
    }

    @Override
    public Version getPluginVersion() {
        String version = PluginManager.getPlugin(PluginId.getId("com.dynatrace.integration.idea")).getVersion();
        String[] split = version.split("\\.");
        return new IIDEDescriptor.Version(split[0], split[1], split[2]);
    }

    @Override
    public void showNotification(String title, String content) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, title, content, NotificationType.ERROR));
        });
    }

    @Override
    public int getId() {
        return 0; //0 - Eclipse, 5 will be later implemented as intellij
    }

    @Override
    public void jumpToClass(String className, String methodName, Callback<Boolean> cb) {
        //we need to jump on UI thread
        ApplicationManager.getApplication().invokeLater(() -> {
            PsiClass clazz = JavaPsiFacade.getInstance(this.project).findClass(className, GlobalSearchScope.allScope(this.project));
            if (clazz == null) {
                cb.call(false);
                return;
            }

            if (!clazz.canNavigateToSource()) {
                cb.call(false);
                return;
            }

            PsiMethod[] method = clazz.findMethodsByName(methodName, false);
            if (method == null || method.length == 0) {
                cb.call(false);
                return;
            }

            if(method[0].canNavigateToSource()) {
                method[0].navigate(true);
            }
            cb.call(method[0].canNavigateToSource());
        });
    }
}
