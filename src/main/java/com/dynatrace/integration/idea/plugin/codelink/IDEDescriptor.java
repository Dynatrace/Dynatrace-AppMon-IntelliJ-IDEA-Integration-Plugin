package com.dynatrace.integration.idea.plugin.codelink;

import com.dynatrace.diagnostics.codelink.IIDEDescriptor;
import com.dynatrace.integration.idea.Icons;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;


public class IDEDescriptor implements IIDEDescriptor {
    public static final NotificationGroup IMPORTANT_NOTIFICATION_GROUP = new NotificationGroup("dynatrace.eventlog", NotificationDisplayType.STICKY_BALLOON, true, null, Icons.CROSSED);
    public static final NotificationGroup INFO_NOTIFICATION_GROUP = new NotificationGroup("dynatrace.systemlog", NotificationDisplayType.NONE, true, null, Icons.DYNATRACE13);

    public static IDEDescriptor getInstance() {
        return ServiceManager.getService(IDEDescriptor.class);
    }

    private final DynatraceSettingsProvider provider;

    public IDEDescriptor(DynatraceSettingsProvider provider) {
        this.provider = provider;
    }

    @Override
    @NotNull
    public String getVersion() {
        return ApplicationInfo.getInstance().getFullVersion();
    }

    @Override
    @NotNull
    public Version getPluginVersion() {
        String version = PluginManager.getPlugin(PluginId.getId("com.dynatrace.integration.idea")).getVersion();
        String[] split = version.split("\\.");
        if (split.length < 3) {
            throw new RuntimeException("Invalid plugin version, should be in major.minor.rev format");
        }
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
        if (this.provider.getState().getCodeLink().isLegacy()) {
            return 0;
        } else {
            return 5;
        }
    }

}
