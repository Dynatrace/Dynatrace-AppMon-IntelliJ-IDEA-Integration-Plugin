package com.dynatrace.diagnostics.codelink;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IProjectDescriptor {

    @NotNull
    String getProjectName();

    @NotNull
    String getProjectPath();

    void jumpToClass(@NotNull CodeLinkLookupResponse response, @Nullable Callback<Boolean> cb);
}
