package com.dynatrace.integration.idea.plugin.settings;

import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
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
    public String getHost() {
        return host;
    }

    public void setHost(@NotNull String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSSL() {
        return ssl;
    }

    public void setSSL(boolean ssl) {
        this.ssl = ssl;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(@NotNull int timeout) {
        this.timeout = timeout;
    }
}