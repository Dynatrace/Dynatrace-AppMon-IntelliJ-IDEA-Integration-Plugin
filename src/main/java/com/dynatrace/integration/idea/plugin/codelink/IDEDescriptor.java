package com.dynatrace.integration.idea.plugin.codelink;

import com.dynatrace.diagnostics.codelink.Callback;
import com.dynatrace.diagnostics.codelink.IIDEDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;

import javax.swing.*;
import java.util.logging.Level;


public class IDEDescriptor implements IIDEDescriptor {
    public static final Icon CROSSED_ICON = IconLoader.getIcon("/icons/crossed_logo.png");
    public static final Icon DYNATRACE_ICON = IconLoader.getIcon("/icons/dynatrace_13.png");
    public static final NotificationGroup IMPORTANT_NOTIFICATION_GROUP = new NotificationGroup("dynatrace.eventlog", NotificationDisplayType.STICKY_BALLOON, true, null, CROSSED_ICON);
    public static final NotificationGroup INFO_NOTIFICATION_GROUP = new NotificationGroup("dynatrace.systemlog", NotificationDisplayType.NONE, true, null, DYNATRACE_ICON);


    public static IDEDescriptor getInstance(Project project) {
        return ServiceManager.getService(project, IDEDescriptor.class);
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
    public void log(Level level, String title, String subtitle, String content, boolean notification) {
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
    public void jumpToClass(String className, String methodName, Callback<Boolean> cb) {
        //we need to jump on UI thread
        ApplicationManager.getApplication().invokeLater(() -> {
            PsiClass clazz = JavaPsiFacade.getInstance(this.project).findClass(className, GlobalSearchScope.allScope(this.project));
            if (clazz == null) {
                if (cb != null) {
                    cb.call(false);
                }
                return;
            }

            if (!clazz.canNavigateToSource()) {
                if (cb != null) {
                    cb.call(false);
                }
                return;
            }

            PsiMethod[] method = clazz.findMethodsByName(methodName, false);
            if (method.length == 0) {
                if (cb != null) {
                    cb.call(false);
                }
                return;
            }

            if (method[0].canNavigateToSource()) {
                method[0].navigate(true);
            }

            if (cb == null) {
                return;
            }
            cb.call(method[0].canNavigateToSource());
        });
    }
}
