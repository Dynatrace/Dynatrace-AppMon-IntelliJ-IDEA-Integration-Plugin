package com.dynatrace.diagnostics.codelink;

public interface ICodeLinkSettings {
    boolean isEnabled();
    void setEnabled(boolean isEnabled);
    String getHost();
    int getPort();
    boolean isSSL();
}
