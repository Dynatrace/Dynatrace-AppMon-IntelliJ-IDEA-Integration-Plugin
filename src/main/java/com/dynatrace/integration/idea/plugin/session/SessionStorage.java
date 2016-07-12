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
            this.recordings.forEach((base, id) ->{
                //Do it in threaded environment.
                new Thread(() -> {
                    try {
                        this.stopRecording(base, id);
                    } catch (PasswordSafeException e) {
                        LOG.warning(Messages.getMessage("plugin.session.cantend", base.getName(), e.getLocalizedMessage()));
                    } finally {
                        cdl.countDown();
                    }
                },"SessionDisposalThread").start();
            });
            try {
                cdl.await(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE,Messages.getMessage("plugin.session.disposalerror"));
            }
            this.recordings.clear();
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

        LOG.info(Messages.getMessage("plugin.session.starting", profileName));
        String id = endpoint.startRecording(profileName, sessionName, sessionName, "all", false, true);
        if (id != null) {
            synchronized (this.recordings) {
                this.recordings.put(base, id);
                LOG.info(Messages.getMessage("plugin.session.started", id, profileName));
            }
        }
        return id;
    }

    public String stopRecording(RunConfigurationBase base, String sessionId) throws PasswordSafeException {
        RESTEndpoint endpoint = DynatraceSettingsProvider.endpointFromState(this.provider.getState());
        LOG.info(Messages.getMessage("plugin.session.stopping", sessionId));
        String stopped = endpoint.stopRecording(sessionId);
        if (stopped != null) {
            synchronized (this.recordings) {
                this.recordings.remove(base);
                LOG.info(Messages.getMessage("plugin.session.stopped", sessionId));
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
