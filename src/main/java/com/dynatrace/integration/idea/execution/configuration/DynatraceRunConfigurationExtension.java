package com.dynatrace.integration.idea.execution.configuration;

import com.dynatrace.diagnostics.automation.rest.sdk.RESTEndpoint;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.execution.DynatraceRunnerSettings;
import com.dynatrace.integration.idea.plugin.session.SessionStorage;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DynatraceRunConfigurationExtension extends RunConfigurationExtension {
    public static final Logger LOG = Logger.getLogger(DynatraceRunConfigurationExtension.class.getName());
//    private static final List<Class<? extends ConfigurationType>> SUPPORTED_TYPES = new ArrayList<>();
//    static {
//        try {
//            SUPPORTED_TYPES.add((Class<? extends ConfigurationType>) ClassLoader.getSystemClassLoader().loadClass("com.intellij.execution.junit.JUnitConfigurationType"));
//        } catch (ClassNotFoundException e) {
//            System.out.println(e.toString());
//        }
//        try {
//            SUPPORTED_TYPES.add((Class<? extends ConfigurationType>) ClassLoader.getSystemClassLoader().loadClass("com.theoryinpractice.testng.configuration.TestNGConfigurationType"));
//        } catch (ClassNotFoundException e) {
//            System.out.println(e.toString());
//        }
//    }

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
            if(executionSettings.isRecordSessionPerLaunch() && !ss.isRecording(configuration)) {
                ss.startRecording(configuration, executionSettings.getSystemProfile());
            }

            StringBuilder builder = new StringBuilder("-agentpath:");
            builder.append(settings.agent.agentLibrary).append('=');
            builder.append("wait=").append(settings.server.timeout).append(',');
            builder.append("name=").append(executionSettings.getAgentName()).append(',');
            builder.append("server=").append(settings.agent.collectorHost).append(',');
            builder.append("port=").append(settings.agent.collectorPort);

            Calendar now = Calendar.getInstance();

            //fetch test id
            String id = endpoint.startTest(executionSettings.getSystemProfile(), String.valueOf(now.get(Calendar.YEAR)), String.valueOf(now.get(Calendar.MONTH) + 1), String.valueOf(now.get(Calendar.DAY_OF_WEEK)), String.valueOf(new SimpleDateFormat("HH:mm:ss").format(now.getTime())), null, null, null, null, null, null);

            LOG.info("================" + id + "===================");
            builder.append(',').append("optionTestRunIdJava=").append(id);
            LOG.info(Messages.getMessage("execution.configuration.tests.running", id));

            //mutate java parameters
            javaParameters.getVMParametersList().add(builder.toString());
        } catch (Exception e) {
            Notifications.Bus.notify(new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID, Messages.getMessage("notifications.error.title"), Messages.getMessage("notifications.error.configuration") + e.getLocalizedMessage(), NotificationType.ERROR));
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
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
