package com.dynatrace.diagnostics.codelink;

import org.jetbrains.annotations.NotNull;

public class ClientVersion implements Comparable<ClientVersion> {
    public final int major;
    public final int minor;
    public final int revision;
    public final int build;

    public static ClientVersion fromString(String version) {
        String[] parts = version.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Provided version is in invalid format, should be M.m.r.b");
        }
        return new ClientVersion(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }

    @Override
    public int hashCode() {
        int result = this.major;
        result = 31 * result + this.minor;
        result = 31 * result + this.revision;
        result = 31 * result + this.build;
        return result;
    }

    public ClientVersion(int major, int minor, int revision, int build) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.build = build;
    }

    @Override
    public int compareTo(@NotNull ClientVersion o) {
        if (o.major > this.major) {
            return -1;
        } else if (o.major < this.major) {
            return 1;
        }
        if (o.minor > this.minor) {
            return -1;
        } else if (o.minor < this.minor) {
            return 1;
        }
        if (o.revision > this.revision) {
            return -1;
        } else if (o.revision < this.revision) {
            return 1;
        }
        if (o.build > this.build) {
            return -1;
        } else if (o.build < this.build) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        ClientVersion that = (ClientVersion) o;

        if (this.major != that.major) return false;
        if (this.minor != that.minor) return false;
        if (this.revision != that.revision) return false;
        return this.build == that.build;
    }
}
