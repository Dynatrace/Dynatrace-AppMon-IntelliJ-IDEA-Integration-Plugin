package com.dynatrace.diagnostics.codelink;

import com.dynatrace.diagnostics.codelink.exceptions.CodeLinkConnectionException;
import org.jetbrains.annotations.NotNull;

import java.net.UnknownHostException;
import java.util.logging.Level;

class PollingWorker implements Runnable {
    private final IProjectDescriptor project;
    private final CodeLinkEndpoint endpoint;
    private final ICodeLinkSettings clSettings;
    private final IIDEDescriptor ide;

    private boolean hasErrored = false;
    private int suppress = 0;
    private long sessionId = -1;

    PollingWorker(@NotNull IIDEDescriptor ide, @NotNull ICodeLinkSettings clSettings, @NotNull IProjectDescriptor project) {
        this.project = project;
        this.endpoint = new CodeLinkEndpoint(project, ide, clSettings);
        this.clSettings = clSettings;
        this.ide = ide;
    }

    @Override
    public void run() {
        if (!this.clSettings.isEnabled()) {
            return;
        }

        // Not a perfect solution.
        if (this.suppress > 0) {
            this.suppress--;
            return;
        }

        try {
            CodeLinkLookupResponse response = this.endpoint.connect(this.sessionId);
            this.sessionId = response.sessionId;
            //if the response times out it means there's no codelink request
            if (response.timedOut) {
                return;
            }
            final long sid = this.sessionId;
            //try to jump to class
            this.project.jumpToClass(response, (b) -> {
                if (!b) {
                    this.ide.log(Level.WARNING, "CodeLink Error", "Method not found", "A method with a given signature could not be found.", false);
                }
                try {
                    this.endpoint.respond(b ? CodeLinkEndpoint.ResponseStatus.FOUND : CodeLinkEndpoint.ResponseStatus.NOT_FOUND, sid);
                } catch (CodeLinkConnectionException e) {
                    this.ide.log(Level.WARNING, "CodeLink Error", "Could not send response", "Error occured while sending a response to Dynatrace Client", false);
                    CodeLinkClient.LOGGER.warning("Could not send response to CodeLink: " + e.getMessage());
                }
            });

            this.hasErrored = false;
        } catch (CodeLinkConnectionException e) {
            CodeLinkClient.LOGGER.warning("Error occured in codelink worker during connection phase" + e.getMessage());
            //if the host can't be found disable codelink to not disturb user with future notifications
            if (e.getCause() instanceof UnknownHostException) {
                this.clSettings.setEnabled(false);
                this.ide.log(Level.WARNING, "CodeLink Error", "Could not connect to client.", "CodeLink has been disabled<br><b>Check your configuration</b>", true);
            } else {
                this.ide.log(Level.WARNING, "CodeLink Error", "Check your configuration", "Failed connecting to Dynatrace AppMon Client to poll for CodeLink jump requests.", false);
                this.suppress = 5;
            }
        } catch (Exception e) {
            CodeLinkClient.LOGGER.warning("Error occured in codelink worker " + e.getMessage());
            if (!hasErrored) {
                this.ide.log(Level.WARNING, "CodeLink Error", "Could not connect to client.", "<b>Check your configuration</b>", true);
            }
            this.hasErrored = true;
            //skip 5 connections
            this.suppress = 5;
        }
    }
}


