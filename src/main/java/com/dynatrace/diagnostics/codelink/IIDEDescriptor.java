package com.dynatrace.diagnostics.codelink;

import java.util.logging.Level;

public interface IIDEDescriptor {
    String getVersion();
    String getProjectName();
    String getProjectPath();
    Version getPluginVersion();
    void log(Level level, String title, String subtitle, String content, boolean notification);
    int getId();
    void jumpToClass(String className, String methodName, Callback<Boolean> cb);

    class Version {
        public final String major;
        public final String minor;
        public final String revision;

        public Version(String major, String minor, String revision) {
            this.major = major;
            this.minor = minor;
            this.revision = revision;
        }
    }
}
