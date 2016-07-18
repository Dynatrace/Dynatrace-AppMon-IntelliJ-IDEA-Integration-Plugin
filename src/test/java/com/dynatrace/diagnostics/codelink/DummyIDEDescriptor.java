package com.dynatrace.diagnostics.codelink;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class DummyIDEDescriptor implements IIDEDescriptor {
    @NotNull
    @Override
    public String getVersion() {
        return "2016.1.2";
    }

    @NotNull
    @Override
    public Version getPluginVersion() {
        return new Version("0", "0", "1");
    }

    @Override
    public void log(@NotNull Level level, @NotNull String title, @Nullable String subtitle, @NotNull String content, boolean notification) {

    }

    @Override
    public int getId() {
        return 0;
    }
}
