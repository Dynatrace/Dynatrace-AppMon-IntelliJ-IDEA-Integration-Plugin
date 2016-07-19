/*
 *  Dynatrace IntelliJ IDEA Integration Plugin
 *  Copyright (c) 2008-2016, DYNATRACE LLC
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  * Neither the name of the dynaTrace software nor the names of its contributors
 *  may be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 *  BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 *
 */

package com.dynatrace.integration.idea.plugin.codelink;

import com.dynatrace.diagnostics.codelink.CodeLinkClient;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.intellij.openapi.components.ProjectComponent;
import org.jetbrains.annotations.NotNull;

public class CodeLinkCoordinator implements ProjectComponent {
    private final DynatraceSettingsProvider settings;
    private final IDEDescriptor ideDescriptor;
    private final ProjectDescriptor projectDescriptor;
    private CodeLinkClient client;

    public CodeLinkCoordinator(DynatraceSettingsProvider settings, IDEDescriptor ideDescriptor, ProjectDescriptor projectDescriptor) {
        this.settings = settings;
        this.ideDescriptor = ideDescriptor;
        this.projectDescriptor = projectDescriptor;
    }

    @Override
    public void projectOpened() {
        this.client = new CodeLinkClient(this.settings.getState().getCodeLink(), this.ideDescriptor, this.projectDescriptor);
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
