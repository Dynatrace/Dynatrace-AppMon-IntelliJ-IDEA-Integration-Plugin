package com.dynatrace.diagnostics.automation.rest.sdk;

import org.jetbrains.annotations.NotNull;

public interface IServerSettings {
    @NotNull
    String getHost();

    int getPort();

    String getPassword();

    boolean isSSL();

    @NotNull
    String getLogin();

    @NotNull
    int getTimeout();
}
