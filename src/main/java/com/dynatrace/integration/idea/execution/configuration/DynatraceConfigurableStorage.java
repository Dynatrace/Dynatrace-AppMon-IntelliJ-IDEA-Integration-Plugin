package com.dynatrace.integration.idea.execution.configuration;


import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
//Extensions modules uses JDOMExternalizable, we can't do much about that AFAIK
public class DynatraceConfigurableStorage implements JDOMExternalizable {
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

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        if(element.getAttributeValue("systemProfile") != null) {
            this.systemProfile = element.getAttributeValue("systemProfile");
        }
        if(element.getAttributeValue("agentName") != null) {
            this.agentName = element.getAttributeValue("agentName");
        }
        this.additionalParameters = element.getAttributeValue("additionalParameters");
        String recordSessionPerLaunch = element.getAttributeValue("recordSessionPerLaunch");
        if (recordSessionPerLaunch != null) {
            this.recordSessionPerLaunch = Boolean.valueOf(recordSessionPerLaunch);
        }
    }

    @Override
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
