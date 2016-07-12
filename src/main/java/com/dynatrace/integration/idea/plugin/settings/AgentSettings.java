package com.dynatrace.integration.idea.plugin.settings;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class AgentSettings {
    @NotNull
    private String agentLibrary = "";
    @NotNull
    private String collectorHost = "localhost";
    private int collectorPort = 9998;

    @NotNull
    public String getCollectorHost() {
        return collectorHost;
    }

    public void setCollectorHost(@NotNull String collectorHost) {
        this.collectorHost = collectorHost;
    }

    @NotNull
    public String getAgentLibrary() {
        return agentLibrary;
    }

    public void setAgentLibrary(@NotNull String agentLibrary) {
        this.agentLibrary = agentLibrary;
    }

    public int getCollectorPort() {
        return collectorPort;
    }

    public void setCollectorPort(int collectorPort) {
        this.collectorPort = collectorPort;
    }

}
