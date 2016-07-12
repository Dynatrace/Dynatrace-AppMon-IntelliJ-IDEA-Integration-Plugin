package com.dynatrace.integration.idea.plugin.settings;

import com.dynatrace.diagnostics.codelink.ICodeLinkSettings;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class CodeLinkSettings implements ICodeLinkSettings {
    private boolean enabled = true;
    @NotNull
    private String host = "localhost";
    private int port = 8031;
    private boolean ssl = true;
    private boolean javaBrowsingPerspective = false;

    public synchronized void setJavaBrowsingPerspective(boolean javaBrowsingPerspective) {
        this.javaBrowsingPerspective = javaBrowsingPerspective;
    }

    public synchronized void setPort(int port) {
        this.port = port;
    }

    public synchronized void setSSL(boolean ssl) {
        this.ssl = ssl;
    }

    public synchronized void setHost(@NotNull String host) {
        this.host = host;
    }

    @Override
    public synchronized boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public synchronized void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
    }

    @Override
    public synchronized String getHost() {
        return this.host;
    }

    @Override
    public synchronized int getPort() {
        return this.port;
    }

    @Override
    public synchronized boolean isSSL() {
        return this.ssl;
    }

    @Override
    public synchronized boolean isJavaBrowsingPerspective() {
        return this.javaBrowsingPerspective;
    }
}
