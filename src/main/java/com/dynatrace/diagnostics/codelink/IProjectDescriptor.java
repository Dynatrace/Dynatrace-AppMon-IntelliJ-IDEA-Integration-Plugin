package com.dynatrace.diagnostics.codelink;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IProjectDescriptor {

    @NotNull
    String getProjectName();

    @NotNull
    String getProjectPath();

    void jumpToClass(@NotNull String className, @Nullable String methodName, @Nullable Callback<Boolean> cb);
}
