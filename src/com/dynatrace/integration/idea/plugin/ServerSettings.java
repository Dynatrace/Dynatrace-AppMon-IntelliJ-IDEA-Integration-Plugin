package com.dynatrace.integration.idea.plugin;

import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import org.jetbrains.annotations.NotNull;

public class ServerSettings {
    public static final String DEFAULT_PASSWORD = "admin";

    @NotNull
    public String host = "localhost";
    public int restPort = 8021;
    public boolean ssl = true;
    @NotNull
    public String login = "admin";
    @NotNull
    //in seconds
    public int timeout = 30;

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.restPort;
    }

    public boolean isConnectionSSL() {
        return this.ssl;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() throws PasswordSafeException {
        //shame on you intelliJ
        String pwd = PasswordSafe.getInstance().getPassword(null, ServerSettings.class, "password");
        return pwd != null ? pwd : DEFAULT_PASSWORD;
    }

    public void setPassword(String password) throws PasswordSafeException {
        PasswordSafe.getInstance().storePassword(null, ServerSettings.class, "password", password);
    }

    public int getTimeout() {
        return this.timeout;
    }
}
