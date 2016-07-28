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

package com.dynatrace.integration.idea.plugin.session;

import com.dynatrace.integration.idea.Messages;
import com.dynatrace.integration.idea.plugin.SDKClient;
import com.dynatrace.integration.idea.plugin.IDEADescriptor;
import com.dynatrace.integration.idea.plugin.settings.DynatraceSettingsProvider;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.sessions.Sessions;
import com.dynatrace.sdk.server.sessions.models.RecordingOption;
import com.dynatrace.sdk.server.sessions.models.StartRecordingRequest;
import com.intellij.openapi.components.ProjectComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionStorage implements ProjectComponent {
    public static final Logger LOG = Logger.getLogger("#" + SessionStorage.class.getName());
    private static final String SESSION_ALREADY_STARTED = "Error starting recording: Session Recording could not be started because it is already started";

    private final HashSet<String> recordings = new HashSet<>();
    private final DynatraceSettingsProvider provider;
    private final Sessions sessions;

    public SessionStorage(@NotNull DynatraceSettingsProvider provider) {
        this.provider = provider;
        this.sessions = new Sessions(SDKClient.getInstance());
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

    @Nullable
    public String startRecording(String profileName) {
        String sessionName = profileName + ' ' + DateFormat.getDateInstance().format(new Date());
        try {
            StartRecordingRequest request = new StartRecordingRequest(profileName);
            request.setPresentableName(sessionName);
            request.setDescription(sessionName);
            request.setRecordingOption(RecordingOption.ALL);
            request.setSessionLocked(false);
            request.setTimestampAllowed(true);
            String id = this.sessions.startRecording(request);

            if (id != null) {
                synchronized (this.recordings) {
                    this.recordings.add(profileName);
                    IDEADescriptor.getInstance().log(Level.INFO, "Session", "", Messages.getMessage("plugin.session.started", id, profileName), false);
                    LOG.info(Messages.getMessage("plugin.session.started", id, profileName));
                }
            }
            return id;
        } catch (ServerConnectionException | ServerResponseException e) {
            if (e.getMessage().equals(SESSION_ALREADY_STARTED)) {
                synchronized (this.recordings) {
                    this.recordings.add(profileName);
                }
                IDEADescriptor.getInstance().log(Level.WARNING, "Session", "", e.getMessage(), false);
                LOG.log(Level.INFO, e.getMessage());
            } else {
                IDEADescriptor.getInstance().log(Level.SEVERE, "Session", "", Messages.getMessage("plugin.session.starting.failed", profileName, e.getMessage()), true);
                LOG.log(Level.INFO, e.getMessage());
            }
        }
        return null;
    }

    @Nullable
    public String stopRecording(String profileName) {
        IDEADescriptor.getInstance().log(Level.WARNING, "Session", "", Messages.getMessage("plugin.session.stopping", profileName), false);
        LOG.info(Messages.getMessage("plugin.session.stopping", profileName));
        try {
            String stopped = this.sessions.stopRecording(profileName);
            if (stopped != null) {
                LOG.info(Messages.getMessage("plugin.session.stopped", profileName));
            }
            return stopped;
        } catch (ServerResponseException | ServerConnectionException e) {
            IDEADescriptor.getInstance().log(Level.WARNING, "Session", "", Messages.getMessage("plugin.session.cantend", profileName, e.getMessage()), true);
            LOG.warning(Messages.getMessage("plugin.session.cantend", profileName, e.getMessage()));
        } finally {
            synchronized (this.recordings) {
                this.recordings.remove(profileName);
            }
        }
        return null;
    }

    public boolean isRecording(String profile) {
        synchronized (this.recordings) {
            return this.recordings.contains(profile);
        }
    }
}
