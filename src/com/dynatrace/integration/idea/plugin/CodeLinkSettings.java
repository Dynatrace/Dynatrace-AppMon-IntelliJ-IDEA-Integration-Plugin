package com.dynatrace.integration.idea.plugin;

import org.jetbrains.annotations.NotNull;

public class CodeLinkSettings {
    public boolean enabled = true;
    @NotNull
    public String host = "localhost";
    public int port = 8031;
    public boolean ssl = true;
    public boolean javaBrowsingPerspective = false;
}
