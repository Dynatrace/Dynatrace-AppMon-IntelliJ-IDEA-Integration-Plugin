package com.dynatrace.integration.idea.plugin.codelink;

import com.dynatrace.diagnostics.codelink.CodeLinkClient;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.intellij.openapi.components.ProjectComponent;
import org.jetbrains.annotations.NotNull;

public class CodeLinkCoordinator implements ProjectComponent {
    private final DynatraceSettingsProvider settings;
    private final IDEDescriptor descriptor;
    private CodeLinkClient client;

    public CodeLinkCoordinator(DynatraceSettingsProvider settings, IDEDescriptor descriptor) {
        this.settings = settings;
        this.descriptor = descriptor;
    }

    @Override
    public void projectOpened() {
        this.client = new CodeLinkClient(this.settings.getState().getCodeLink(), this.descriptor);
        this.client.startPolling(CodeLinkClient.DEFAULT_INTERVAL, CodeLinkClient.DEFAULT_UNIT);
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
        this.client.stopPolling();
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "dynatrace.codelinkcoordinator";
    }
}
