package com.dynatrace.integration.idea.plugin.session;

import com.dynatrace.diagnostics.automation.rest.sdk.RESTEndpoint;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.openapi.components.ProjectComponent;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionStorage implements ProjectComponent {
    public static final Logger LOG = Logger.getLogger(SessionStorage.class.getName());

    private HashMap<RunConfigurationBase, String> recordings = new HashMap<>();
    private final DynatraceSettingsProvider provider;

    public SessionStorage(DynatraceSettingsProvider provider) {
        this.provider = provider;
    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {
        synchronized (this.recordings) {
            CountDownLatch cdl = new CountDownLatch(this.recordings.size());
            for (Map.Entry<RunConfigurationBase, String> entry : this.recordings.entrySet()) {
                //Do it in threaded environment.
                new Thread(() -> {
                    try {
                        this.stopRecording(entry.getKey(), entry.getValue());
                    } catch (PasswordSafeException e) {
                        LOG.warning(Messages.getMessage("plugin.session.cantend", entry.getKey().getName(), e.getLocalizedMessage()));
                    } finally {
                        cdl.countDown();
                    }
                },"SessionDisposalThread").start();
            }
            try {
                cdl.await(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE,Messages.getMessage("plugin.session.disposalerror"));
            }
        }
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "dynatrace.sessionstorage";
    }

    public String startRecording(RunConfigurationBase base, String profileName) throws PasswordSafeException {
        RESTEndpoint endpoint = DynatraceSettingsProvider.endpointFromState(this.provider.getState());
        String sessionName = profileName + ' ' + DateFormat.getDateInstance().format(new Date());
        String id = endpoint.startRecording(profileName, sessionName, sessionName, "all", false, true);
        if (id != null) {
            synchronized (this.recordings) {
                this.recordings.put(base, id);
            }
        }
        return id;
    }

    public String stopRecording(RunConfigurationBase base, String sessionId) throws PasswordSafeException {
        RESTEndpoint endpoint = DynatraceSettingsProvider.endpointFromState(this.provider.getState());
        String stopped = endpoint.stopRecording(sessionId);
        if (stopped != null) {
            synchronized (this.recordings) {
                this.recordings.remove(base);
            }
        }
        return stopped;
    }

    public boolean isRecording(RunConfigurationBase base) {
        synchronized (this.recordings) {
            return this.recordings.containsKey(base);
        }
    }
}
