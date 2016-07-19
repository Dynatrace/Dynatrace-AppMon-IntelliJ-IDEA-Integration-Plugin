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

import com.dynatrace.diagnostics.codelink.ICodeLinkSettings;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class CodeLinkSettings implements ICodeLinkSettings {
    private boolean enabled = true;
    @NotNull
    private String host = "localhost";
    private int port = 8031;
    private boolean ssl = true;
    private boolean isLegacy = true;

    @Override
    public synchronized boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public synchronized void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
    }

    @Override
    public synchronized String getHost() {
        return this.host;
    }

    public synchronized void setHost(@NotNull String host) {
        this.host = host;
    }

    @Override
    public synchronized int getPort() {
        return this.port;
    }

    public synchronized void setPort(int port) {
        this.port = port;
    }

    @Override
    public synchronized boolean isSSL() {
        return this.ssl;
    }

    public synchronized void setSSL(boolean ssl) {
        this.ssl = ssl;
    }

    public synchronized boolean isLegacy() {
        return this.isLegacy;
    }

    public synchronized void setLegacy(boolean legacy) {
        this.isLegacy = legacy;
    }
}
