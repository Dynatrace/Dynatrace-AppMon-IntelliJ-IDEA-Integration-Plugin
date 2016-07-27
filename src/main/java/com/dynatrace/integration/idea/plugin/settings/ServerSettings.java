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

import com.dynatrace.server.sdk.ServerConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.security.Principal;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServerSettings implements ServerConfiguration, Principal {

    @NotNull
    private String host = "localhost";
    private int port = 8021;
    private String password = "admin";
    private boolean ssl = true;
    @NotNull
    private String login = "admin";
    //in milliseconds
    private int timeout = 30000;

    @NotNull
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
    public Principal getUserPrincipal() {
        return this;
    }

    @Override
    public synchronized String getPassword() {
        return this.password;
    }

    public synchronized void setPassword(String password) {
        this.password = password;
    }

    @Override
    public synchronized boolean isSSL() {
        return this.ssl;
    }

    public synchronized void setSSL(boolean ssl) {
        this.ssl = ssl;
    }


    public synchronized void setLogin(@NotNull String login) {
        this.login = login;
    }

    @Override
    public synchronized int getTimeout() {
        return this.timeout;
    }

    @Override
    public boolean isValidateCertificates() {
        return false;
    }

    public synchronized void setTimeout(int timeoutMillis) {
        this.timeout = timeoutMillis;
    }

    @Override
    public String getName() {
        return this.login;
    }
}