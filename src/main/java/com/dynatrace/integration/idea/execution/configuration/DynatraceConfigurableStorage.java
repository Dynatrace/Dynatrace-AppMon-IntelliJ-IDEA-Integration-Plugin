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

package com.dynatrace.integration.idea.execution.configuration;


import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class DynatraceConfigurableStorage {
    public static final Key<DynatraceConfigurableStorage> STORAGE_KEY = Key.create("com.dynatrace.integration.idea");

    private String systemProfile = "IntelliJ";
    private String agentName = "IntelliJ";
    private String additionalParameters;
    private boolean recordSessionPerLaunch;

    static DynatraceConfigurableStorage getOrCreateStorage(@NotNull RunConfigurationBase runConfiguration) {
        DynatraceConfigurableStorage storage = runConfiguration.getCopyableUserData(STORAGE_KEY);
        if (storage == null) {
            storage = new DynatraceConfigurableStorage();
        }
        runConfiguration.putCopyableUserData(STORAGE_KEY, storage);
        return storage;
    }

    public void readExternal(Element element) throws InvalidDataException {
        if (element.getAttributeValue("systemProfile") != null) {
            this.systemProfile = element.getAttributeValue("systemProfile");
        }
        if (element.getAttributeValue("agentName") != null) {
            this.agentName = element.getAttributeValue("agentName");
        }
        this.additionalParameters = element.getAttributeValue("additionalParameters");
        String recordSessionPerLaunch = element.getAttributeValue("recordSessionPerLaunch");
        if (recordSessionPerLaunch != null) {
            this.recordSessionPerLaunch = Boolean.valueOf(recordSessionPerLaunch);
        }
    }

    public void writeExternal(Element element) throws WriteExternalException {
        if (this.systemProfile != null) {
            element.setAttribute("systemProfile", this.systemProfile);
        }
        if (this.agentName != null) {
            element.setAttribute("agentName", this.agentName);
        }
        if (this.additionalParameters != null) {
            element.setAttribute("additionalParameters", this.additionalParameters);
        }
        element.setAttribute("recordSessionPerLaunch", String.valueOf(recordSessionPerLaunch));
    }

    public String getSystemProfile() {
        return this.systemProfile;
    }

    public void setSystemProfile(String systemProfile) {
        this.systemProfile = systemProfile;
    }

    public String getAgentName() {
        return this.agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAdditionalParameters() {
        return this.additionalParameters;
    }

    public void setAdditionalParameters(String additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    public boolean isRecordSessionPerLaunch() {
        return this.recordSessionPerLaunch;
    }

    public void setRecordSessionPerLaunch(boolean recordSessionPerLaunch) {
        this.recordSessionPerLaunch = recordSessionPerLaunch;
    }
}
