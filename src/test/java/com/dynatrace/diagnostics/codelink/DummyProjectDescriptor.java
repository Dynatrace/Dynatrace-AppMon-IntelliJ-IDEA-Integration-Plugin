package com.dynatrace.diagnostics.codelink;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DummyProjectDescriptor implements IProjectDescriptor {
    @NotNull
    @Override
    public String getProjectName() {
        return "DummyProject";
    }

    @NotNull
    @Override
    public String getProjectPath() {
        return "/dummy/path";
    }

    @Override
    public void jumpToClass(@NotNull CodeLinkLookupResponse reponse, @Nullable Callback<Boolean> cb) {

    }
}
