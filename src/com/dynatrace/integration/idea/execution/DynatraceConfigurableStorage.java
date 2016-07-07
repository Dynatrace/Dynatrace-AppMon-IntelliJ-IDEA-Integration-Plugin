package com.dynatrace.integration.idea.execution;


import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class DynatraceConfigurableStorage {
    public static final Key<DynatraceConfigurableStorage> STORAGE_KEY = Key.create("com.dynatrace.integration.idea");
    public String test;

    static DynatraceConfigurableStorage getOrCreateStorage(@NotNull RunConfigurationBase runConfiguration) {
        DynatraceConfigurableStorage storage = runConfiguration.getUserData(STORAGE_KEY);
        if (storage == null) {
            System.out.println("No storage saved for " + runConfiguration.toString());
            storage = new DynatraceConfigurableStorage();
        }
        return storage;
    }

    void readExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws InvalidDataException {
        System.out.println("PERFORMING READ..." + runConfiguration.toString());
        this.test = element.getAttributeValue("test");
        if (this.test == null) {
            System.out.println("null");
            int rand = new Random().nextInt();
            this.test = Integer.toString(rand);
        }
        System.out.println("Stored random" + this.test);
    }

    void writeExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws WriteExternalException {
        System.out.println("Performing WRITE for " + runConfiguration.toString());
        if (this.test != null) {
            element.setAttribute("test", this.test);
        } else {
            System.out.println("nulled");
        }
        //TODO move
        runConfiguration.putUserData(DynatraceConfigurableStorage.STORAGE_KEY, this);
    }
}
