package com.dynatrace.integration.idea.plugin.settings;

import org.jetbrains.annotations.NotNull;

public class AgentSettings {
    @NotNull
    public String agentLibrary = "";
    @NotNull
    public String collectorHost = "localhost";
    public int collectorPort = 9998;
}
