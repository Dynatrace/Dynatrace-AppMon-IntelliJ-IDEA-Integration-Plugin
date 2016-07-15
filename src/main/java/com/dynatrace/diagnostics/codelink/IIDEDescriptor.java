package com.dynatrace.diagnostics.codelink;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public interface IIDEDescriptor {
    @NotNull
    String getVersion();

    @NotNull
    Version getPluginVersion();

    void log(@NotNull Level level, @NotNull String title, @Nullable String subtitle, @NotNull String content, boolean notification);

    int getId();


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
