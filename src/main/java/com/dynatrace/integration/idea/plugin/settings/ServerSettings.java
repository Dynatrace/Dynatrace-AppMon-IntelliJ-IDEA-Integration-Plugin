package com.dynatrace.integration.idea.plugin.settings;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServerSettings {
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
    public synchronized String getHost() {
        return host;
    }

    public synchronized void setHost(@NotNull String host) {
        this.host = host;
    }

    public synchronized int getPort() {
        return port;
    }

    public synchronized void setPort(int port) {
        this.port = port;
    }

    public synchronized String getPassword() {
        return password;
    }

    public synchronized void setPassword(String password) {
        this.password = password;
    }

    public synchronized boolean isSSL() {
        return ssl;
    }

    public synchronized void setSSL(boolean ssl) {
        this.ssl = ssl;
    }

    @NotNull
    public synchronized String getLogin() {
        return login;
    }

    public synchronized void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    public synchronized int getTimeout() {
        return timeout;
    }

    public synchronized void setTimeout(@NotNull int timeout) {
        this.timeout = timeout;
    }
}