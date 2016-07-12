package com.dynatrace.integration.idea.plugin.session;

import com.dynatrace.diagnostics.automation.rest.sdk.CommandExecutionException;
import com.dynatrace.diagnostics.automation.rest.sdk.RESTEndpoint;
import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.openapi.components.ProjectComponent;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionStorage implements ProjectComponent {
    public static final Logger LOG = Logger.getLogger(SessionStorage.class.getName());
    private static final String SESSION_ALREADY_STARTED = "Error starting recording: Session Recording could not be started because it is already started";

    private final HashSet<String> recordings = new HashSet<>();
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
            this.recordings.forEach((name) -> {
                //Do it in threaded environment.
                new Thread(() -> {
                    try {
                        this.stopRecording(name);
                    } catch (Exception e) {
                        LOG.warning(Messages.getMessage("plugin.session.cantend", name, e.getLocalizedMessage()));
                    } finally {
                        cdl.countDown();
                    }
                }, "SessionDisposalThread").start();
            });
            try {
                cdl.await(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, Messages.getMessage("plugin.session.disposalerror"));
            }
            this.recordings.clear();
        }
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "dynatrace.sessionstorage";
    }

    public String startRecording(String profileName) throws PasswordSafeException {
        RESTEndpoint endpoint = DynatraceSettingsProvider.endpointFromState(this.provider.getState());
        String sessionName = profileName + ' ' + DateFormat.getDateInstance().format(new Date());

        LOG.info(Messages.getMessage("plugin.session.starting", profileName));
        try {
            String id = endpoint.startRecording(profileName, sessionName, sessionName, "all", false, true);

            if (id != null) {
                synchronized (this.recordings) {
                    this.recordings.add(profileName);
                    LOG.info(Messages.getMessage("plugin.session.started", id, profileName));
                }
            }
        } catch (CommandExecutionException e) {
            if (e.getMessage().equals(SESSION_ALREADY_STARTED)) {
                synchronized (this.recordings) {
                    this.recordings.add(profileName);
                }
                LOG.log(Level.INFO, e.getMessage());
            } else {
                throw e;
            }
        }
        return null;
    }

    public String stopRecording(String profileName) throws PasswordSafeException {
        RESTEndpoint endpoint = DynatraceSettingsProvider.endpointFromState(this.provider.getState());
        LOG.info(Messages.getMessage("plugin.session.stopping", profileName));
        String stopped = endpoint.stopRecording(profileName);
        if (stopped != null) {
            synchronized (this.recordings) {
                this.recordings.remove(profileName);
                LOG.info(Messages.getMessage("plugin.session.stopped", profileName));
            }
        }
        return stopped;
    }

    public boolean isRecording(String profile) {
        synchronized (this.recordings) {
            return this.recordings.contains(profile);
        }
    }
}
