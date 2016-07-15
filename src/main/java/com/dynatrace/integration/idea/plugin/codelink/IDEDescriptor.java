package com.dynatrace.integration.idea.plugin.codelink;

import com.dynatrace.diagnostics.codelink.Callback;
import com.dynatrace.diagnostics.codelink.IIDEDescriptor;
import com.dynatrace.integration.idea.Icons;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;


public class IDEDescriptor implements IIDEDescriptor {
    public static final NotificationGroup IMPORTANT_NOTIFICATION_GROUP = new NotificationGroup("dynatrace.eventlog", NotificationDisplayType.STICKY_BALLOON, true, null, Icons.CROSSED);
    public static final NotificationGroup INFO_NOTIFICATION_GROUP = new NotificationGroup("dynatrace.systemlog", NotificationDisplayType.NONE, true, null, Icons.DYNATRACE13);


    public static IDEDescriptor getInstance(Project project) {
        return ServiceManager.getService(project, IDEDescriptor.class);
    }

    private final Project project;

    public IDEDescriptor(Project project) {
        this.project = project;
    }

    @Override
    @NotNull
    public String getVersion() {
        return ApplicationInfo.getInstance().getFullVersion();
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
    @NotNull
    public Version getPluginVersion() {
        String version = PluginManager.getPlugin(PluginId.getId("com.dynatrace.integration.idea")).getVersion();
        String[] split = version.split("\\.");
        return new IIDEDescriptor.Version(split[0], split[1], split[2]);
    }

    @Override
    public void log(@NotNull Level level, @NotNull String title, @Nullable String subtitle, @NotNull String content, boolean notification) {
        ApplicationManager.getApplication().invokeLater(() -> {
            NotificationType type = NotificationType.INFORMATION;
            if (level == Level.SEVERE) {
                type = NotificationType.ERROR;
            } else if (level == Level.WARNING) {
                type = NotificationType.WARNING;
            }
            Notification notif;
            if (notification) {
                notif = IMPORTANT_NOTIFICATION_GROUP.createNotification(title, subtitle, content, type);
            } else {
                notif = INFO_NOTIFICATION_GROUP.createNotification(title, subtitle, content, type);
            }
            Notifications.Bus.notify(notif);
        });
    }

    @Override
    public int getId() {
        return 0; //0 - Eclipse, 5 will be later implemented as intellij
    }

    @Override
    public void jumpToClass(@NotNull String className, @Nullable String methodName, @Nullable Callback<Boolean> cb) {
        //we need to jump on UI thread
        ApplicationManager.getApplication().invokeLater(() -> {
            Callback<Boolean> callback = cb != null ? cb : (b) -> {
            };
            PsiClass clazz = JavaPsiFacade.getInstance(this.project).findClass(className, GlobalSearchScope.allScope(this.project));
            if (clazz == null || !clazz.canNavigateToSource()) {
                cb.call(false);
                return;
            }

            if (methodName == null) {
                clazz.navigate(true);
                cb.call(true);
                return;
            }

            PsiMethod[] method = clazz.findMethodsByName(methodName, false);
            if (method.length == 0) {
                cb.call(false);
                return;
            }

            if (method[0].canNavigateToSource()) {
                method[0].navigate(true);
            }
            cb.call(method[0].canNavigateToSource());
        });
    }
}
