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

import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.execution.DynatraceRunnerSettings;
import com.dynatrace.integration.idea.plugin.SDKClient;
import com.dynatrace.integration.idea.plugin.codelink.IDEDescriptor;
import com.dynatrace.integration.idea.plugin.session.SessionStorage;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.dynatrace.server.sdk.DynatraceClient;
import com.dynatrace.server.sdk.testautomation.TestAutomation;
import com.dynatrace.server.sdk.testautomation.models.CreateTestRunRequest;
import com.dynatrace.server.sdk.testautomation.models.TestRun;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.JavaTestConfigurationBase;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynatraceRunConfigurationExtension extends RunConfigurationExtension {
    public static final Key<String> PROFILE_KEY = Key.create("com.dynatrace.integration.idea.profilename");
    public static final Key<String> TRID_KEY = Key.create("com.dynatrace.integration.idea.trid");
    private static final Pattern TRID_EXTRACTOR = Pattern.compile("-agentpath:[^\"]+,optionTestRunIdJava=([\\w-]+)");

    @Override
    public void attachToProcess(@NotNull final RunConfigurationBase configuration, @NotNull final ProcessHandler handler, @Nullable RunnerSettings runnerSettings) {
        super.attachToProcess(configuration, handler, runnerSettings);
        if (!(runnerSettings instanceof DynatraceRunnerSettings)) {
            return;
        }
        if (!(handler instanceof OSProcessHandler)) {
            return;
        }

        OSProcessHandler procHandler = (OSProcessHandler) handler;
        DynatraceConfigurableStorage executionSettings = DynatraceConfigurableStorage.getOrCreateStorage(configuration);
        Matcher matcher = TRID_EXTRACTOR.matcher(procHandler.getCommandLine());
        if (!matcher.find()) {
            return;
        }
        handler.putCopyableUserData(PROFILE_KEY, executionSettings.getSystemProfile());
        handler.putCopyableUserData(TRID_KEY, matcher.group(1));
    }


    @Override
    public void updateJavaParameters(RunConfigurationBase configuration, JavaParameters javaParameters, RunnerSettings runnerSettings) throws ExecutionException {
        //Check if the action was ran by DynatraceExecutor
        if (!(runnerSettings instanceof DynatraceRunnerSettings)) {
            return;
        }
        //get global settings
        DynatraceSettingsProvider.State settings = DynatraceSettingsProvider.getInstance().getState();

        try {
            DynatraceClient client = SDKClient.getInstance();
            //get local (runconfiguration) settings
            DynatraceConfigurableStorage executionSettings = DynatraceConfigurableStorage.getOrCreateStorage(configuration);

            SessionStorage ss = configuration.getProject().getComponent(SessionStorage.class);
            if (executionSettings.isRecordSessionPerLaunch() && !ss.isRecording(executionSettings.getSystemProfile())) {
                ss.startRecording(executionSettings.getSystemProfile());
            }

            StringBuilder builder = new StringBuilder("-agentpath:");
            builder.append(settings.getAgent().getAgentLibrary()).append('=');
            builder.append("wait=").append(settings.getServer().getTimeout()).append(',');
            builder.append("name=").append(executionSettings.getAgentName()).append(',');
            builder.append("server=").append(settings.getAgent().getCollectorHost()).append(',');
            builder.append("port=").append(settings.getAgent().getCollectorPort());

            if (executionSettings.getAdditionalParameters() != null && !executionSettings.getAdditionalParameters().isEmpty()) {
                builder.append(',').append(executionSettings.getAdditionalParameters());
            }

            Calendar now = Calendar.getInstance();

            //fetch test id
            TestAutomation testAutomation = new TestAutomation(client);
            CreateTestRunRequest request = new CreateTestRunRequest(executionSettings.getSystemProfile(), String.valueOf(new SimpleDateFormat("HH:mm:ss").format(now.getTime())));
            request.setVersionMajor(String.valueOf(now.get(Calendar.YEAR)));
            request.setVersionMinor(String.valueOf(now.get(Calendar.MONTH) + 1));
            request.setVersionRevision(String.valueOf(now.get(Calendar.DAY_OF_WEEK)));

            TestRun testRun = testAutomation.createTestRun(request);

            builder.append(',').append("optionTestRunIdJava=").append(testRun.getId());
            IDEDescriptor.getInstance().log(Level.INFO, "TestRun", "", Messages.getMessage("execution.configuration.tests.running", testRun.getId()), false);

            //mutate java parameters
            javaParameters.getVMParametersList().add(builder.toString());
        } catch (Exception e) {
            //IDEDescriptor.getInstance(configuration.getProject()).log(Level.SEVERE, Messages.getMessage("notifications.error.title"), "", Messages.getMessage("notifications.error.configuration"), true);
            throw new ExecutionException(e);
        }
    }

    @Override
    protected void readExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws InvalidDataException {
        DynatraceConfigurableStorage.getOrCreateStorage(runConfiguration).readExternal(element);
    }

    @Override
    protected void writeExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws WriteExternalException {
        DynatraceConfigurableStorage.getOrCreateStorage(runConfiguration).writeExternal(element);
    }

    @NotNull
    @Override
    protected String getEditorTitle() {
        return "Dynatrace AppMon";
    }

    @Override
    protected boolean isApplicableFor(@NotNull RunConfigurationBase runConfigurationBase) {
        return runConfigurationBase instanceof JavaTestConfigurationBase;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    protected SettingsEditor<RunConfigurationBase> createEditor(@NotNull RunConfigurationBase base) {
        return new DynatraceExtensionConfigurable();
    }
}
