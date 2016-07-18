package com.dynatrace.diagnostics.automation.rest.sdk;

import org.apache.http.auth.Credentials;
import org.jetbrains.annotations.NotNull;

import java.security.Principal;

public abstract class ServerSettings implements Principal, Credentials {
    @NotNull
    public abstract String getHost();

    public abstract int getPort();

    @Override
    public Principal getUserPrincipal() {
        return this;
    }

    public abstract String getPassword();

    public abstract boolean isSSL();

    @NotNull
    public abstract String getLogin();

    public abstract int getTimeout();

    @Override
    public String getName() {
        return this.getLogin();
    }
}
