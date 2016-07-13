package com.dynatrace.integration.idea.plugin.settings;

import com.dynatrace.diagnostics.automation.rest.sdk.IServerSettings;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServerSettings implements IServerSettings {
    public static final String DEFAULT_PASSWORD = "admin";

    @NotNull
    private String host = "localhost";
    private int port = 8021;
    private String password = "admin";
    private boolean ssl = true;
    @NotNull
    private String login = "admin";
    @NotNull
    //in seconds
    private int timeout = 30;

    @NotNull
    @Override
    public synchronized String getHost() {
        return host;
    }

    public synchronized void setHost(@NotNull String host) {
        this.host = host;
    }

    @Override
    public synchronized int getPort() {
        return port;
    }

    public synchronized void setPort(int port) {
        this.port = port;
    }

    @Override
    public synchronized String getPassword() {
        return password;
    }

    public synchronized void setPassword(String password) {
        this.password = password;
    }

    @Override
    public synchronized boolean isSSL() {
        return ssl;
    }

    public synchronized void setSSL(boolean ssl) {
        this.ssl = ssl;
    }

    @NotNull
    @Override
    public synchronized String getLogin() {
        return login;
    }

    public synchronized void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    @Override
    public synchronized int getTimeout() {
        return timeout;
    }

    public synchronized void setTimeout(@NotNull int timeout) {
        this.timeout = timeout;
    }
}