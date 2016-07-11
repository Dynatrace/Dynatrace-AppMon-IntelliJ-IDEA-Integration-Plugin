package com.dynatrace.diagnostics.codelink;

/**
 * Created by Maciej.Mionskowski on 7/11/2016.
 */
public interface IIDEDescriptor {
    String getVersion();
    String getProjectName();
    String getProjectPath();
    Version getPluginVersion();
    void showNotification(String title, String content);
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
