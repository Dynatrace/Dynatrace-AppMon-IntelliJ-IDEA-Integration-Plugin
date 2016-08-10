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

package com.dynatrace.integration.idea.plugin.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Property;
import org.jetbrains.annotations.NotNull;

@State(name = "DynatraceSettingsProvider", storages = @Storage(file = "dynatrace.settings.xml"))
public class DynatraceSettingsProvider implements PersistentStateComponent<DynatraceSettingsProvider.State> {

    private State state;

    public static DynatraceSettingsProvider getInstance() {
        return ServiceManager.getService(DynatraceSettingsProvider.class);
    }

    @Override
    @NotNull
    public DynatraceSettingsProvider.State getState() {
        if (this.state == null) {
            this.state = new DynatraceSettingsProvider.State();
        }
        return this.state;
    }

    @Override
    public void loadState(DynatraceSettingsProvider.State state) {
        if (state == null) {
            this.state = new DynatraceSettingsProvider.State();
        }
        this.state = state;
    }

    public static class State {
        @NotNull
        @Property
        private ServerSettings server = new ServerSettings();
        @NotNull
        @Property
        private AgentSettings agent = new AgentSettings();
        @NotNull
        @Property
        private CodeLinkSettings codeLink = new CodeLinkSettings();

        @NotNull
        public ServerSettings getServer() {
            return this.server;
        }

        @NotNull
        public AgentSettings getAgent() {
            return this.agent;
        }

        @NotNull
        public CodeLinkSettings getCodeLink() {
            return this.codeLink;
        }
    }
}
