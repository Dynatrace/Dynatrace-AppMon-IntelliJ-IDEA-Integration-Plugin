package com.dynatrace.integration.idea.execution.configuration;

import com.dynatrace.diagnostics.automation.rest.sdk.RESTEndpoint;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.execution.DynatraceProcessListener;
import com.dynatrace.integration.idea.execution.DynatraceRunnerSettings;
import com.dynatrace.integration.idea.execution.result.TestRunResultsCoordinator;
import com.dynatrace.integration.idea.plugin.codelink.IDEDescriptor;
import com.dynatrace.integration.idea.plugin.session.SessionStorage;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

public class DynatraceRunConfigurationExtension extends RunConfigurationExtension {
    @Override
    public void attachToProcess(@NotNull final RunConfigurationBase configuration, @NotNull final ProcessHandler handler, @Nullable RunnerSettings runnerSettings) {
        super.attachToProcess(configuration, handler, runnerSettings);
        if (!(runnerSettings instanceof DynatraceRunnerSettings)) {
            return;
        }
        DynatraceConfigurableStorage executionSettings = DynatraceConfigurableStorage.getOrCreateStorage(configuration);
        handler.addProcessListener(new DynatraceProcessListener(executionSettings.getSystemProfile(), configuration.getProject()));
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
            RESTEndpoint endpoint = DynatraceSettingsProvider.endpointFromState(settings);
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
            String id = endpoint.startTest(executionSettings.getSystemProfile(), String.valueOf(now.get(Calendar.YEAR)),
                    String.valueOf(now.get(Calendar.MONTH) + 1), String.valueOf(now.get(Calendar.DAY_OF_WEEK)),
                    String.valueOf(new SimpleDateFormat("HH:mm:ss").format(now.getTime())),
                    null, null, null, null, null, null);

            builder.append(',').append("optionTestRunIdJava=").append(id);
            IDEDescriptor.getInstance(configuration.getProject()).log(Level.INFO, "TestRun", "", Messages.getMessage("execution.configuration.tests.running", id), false);

            //mutate java parameters
            javaParameters.getVMParametersList().add(builder.toString());

            TestRunResultsCoordinator coordinator = TestRunResultsCoordinator.getInstance(configuration.getProject());
            //register test run in order to display results later in form of a tool window
            coordinator.registerTestRun(executionSettings.getSystemProfile(), id);
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

    @Nullable
    @Override
    protected String getEditorTitle() {
        return "Dynatrace";
    }

    @Override
    protected boolean isApplicableFor(@NotNull RunConfigurationBase runConfigurationBase) {
        //cannot compare class objects, classloader limitations
        return runConfigurationBase.getType().getClass().getCanonicalName().equals("com.intellij.execution.junit.JUnitConfigurationType")
                || runConfigurationBase.getType().getClass().getCanonicalName().equals("com.theoryinpractice.testng.configuration.TestNGConfigurationType");
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    protected SettingsEditor createEditor(@NotNull RunConfigurationBase base) {
        return new DynatraceExtensionConfigurable();
    }
}
