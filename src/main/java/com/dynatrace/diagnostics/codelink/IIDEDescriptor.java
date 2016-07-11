package com.dynatrace.diagnostics.codelink;

/**
 * Created by Maciej.Mionskowski on 7/11/2016.
 */
public interface IIDEDescriptor {
    String getVersion();
    String getProjectName();
    String getProjectPath();
    void showNotification(String title, String content);
    int getId();
    void jumpToClass(String className, String methodName, Callback<Boolean> cb);
}
