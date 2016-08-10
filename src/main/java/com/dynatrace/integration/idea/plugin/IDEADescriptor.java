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

package com.dynatrace.integration.idea.plugin;

import com.dynatrace.codelink.IDEDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;


public class IDEADescriptor implements IDEDescriptor {
    public static final NotificationGroup IMPORTANT_NOTIFICATION_GROUP = NotificationGroup.balloonGroup("dynatrace.eventlog");
    public static final NotificationGroup INFO_NOTIFICATION_GROUP = NotificationGroup.logOnlyGroup("dynatrace.systemlog");
    private static final String NOTIFICATION_FORMAT = "<b>%s</b><br><i>%s</i><br>%s";
    private static final String LOG_FORMAT = "[%s](%s) - %s";

    public static IDEADescriptor getInstance() {
        return ServiceManager.getService(IDEADescriptor.class);
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
        return new IDEDescriptor.Version(split[0], split[1], split[2]);
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
                notif = IMPORTANT_NOTIFICATION_GROUP.createNotification(String.format(NOTIFICATION_FORMAT, title, subtitle, content), type);
            } else {
                notif = INFO_NOTIFICATION_GROUP.createNotification(String.format(LOG_FORMAT, title, subtitle, content), type);
            }
            Notifications.Bus.notify(notif);
        });
    }

    @Override
    public int getId() {
        return IDEDescriptor.IDEA_ID;
    }

}
