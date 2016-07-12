package com.dynatrace.integration.idea.plugin.settings;

import com.dynatrace.diagnostics.codelink.ICodeLinkSettings;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class CodeLinkSettings implements ICodeLinkSettings {
    private boolean enabled = true;
    @NotNull
    private String host = "localhost";
    private int port = 8031;
    private boolean ssl = true;
    private boolean javaBrowsingPerspective = false;

    public void setJavaBrowsingPerspective(boolean javaBrowsingPerspective) {
        this.javaBrowsingPerspective = javaBrowsingPerspective;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSSL(boolean ssl) {
        this.ssl = ssl;
    }

    public void setHost(@NotNull String host) {
        this.host = host;
    }

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
