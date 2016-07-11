package com.dynatrace.integration.idea.plugin.settings;

import com.dynatrace.diagnostics.codelink.ICodeLinkSettings;
import org.jetbrains.annotations.NotNull;

public class CodeLinkSettings implements ICodeLinkSettings {
    public boolean enabled = true;
    @NotNull
    public String host = "localhost";
    public int port = 8031;
    public boolean ssl = true;
    public boolean javaBrowsingPerspective = false;

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public boolean isSSL() {
        return this.ssl;
    }

    @Override
    public boolean isJavaBrowsingPerspective() {
        return this.javaBrowsingPerspective;
    }
}
