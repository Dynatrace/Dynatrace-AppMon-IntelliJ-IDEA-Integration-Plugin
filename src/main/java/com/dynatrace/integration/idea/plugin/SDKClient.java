package com.dynatrace.integration.idea.plugin;

import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.dynatrace.server.sdk.DynatraceClient;
import com.intellij.openapi.components.ServiceManager;

public class SDKClient {

    private final DynatraceClient client;

    public static DynatraceClient getInstance() {
        return ServiceManager.getService(SDKClient.class).getClient();
    }

    public SDKClient(DynatraceSettingsProvider provider) {
        this.client = new DynatraceClient(provider.getState().getServer());
    }

    public DynatraceClient getClient() {
        return this.client;
    }
}
