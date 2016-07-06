package com.dynatrace.integration.idea.plugin;

import org.jetbrains.annotations.NotNull;

public class ServerSettings {
    @NotNull
    public String host = "";
    public int restPort = 8021;
    public boolean ssl = true;
    @NotNull
    public String login = "admin";
    @NotNull
    //in seconds
    public int timeout = 30;
}
