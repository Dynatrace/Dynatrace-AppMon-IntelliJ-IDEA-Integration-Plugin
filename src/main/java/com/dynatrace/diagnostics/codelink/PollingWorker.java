package com.dynatrace.diagnostics.codelink;

import com.dynatrace.diagnostics.Utils;
import com.dynatrace.diagnostics.codelink.exceptions.CodeLinkConnectionException;
import com.dynatrace.diagnostics.codelink.exceptions.CodeLinkResponseException;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

class PollingWorker implements Runnable {
    public static final int RESPONSE_FOUND = 50;
    public static final int RESPONSE_NOT_FOUND = 51;

    private static StringBuilder buildURL(ICodeLinkSettings settings) {
        return new StringBuilder(settings.isSSL() ? "https://" : "http://")
                .append(settings.getHost())
                .append(':').append(settings.getPort())
                .append("/rest/management/codelink/");
    }

    private final CloseableHttpClient client;
    private final ICodeLinkSettings clSettings;
    private final IIDEDescriptor ide;

    private long sessionId = -1;
    private boolean hasErrored = false;
    private int suppress = 0;

    public PollingWorker(IIDEDescriptor ide, ICodeLinkSettings clSettings) {
        this.ide = ide;
        this.clSettings = clSettings;
        this.client = Utils.clientBuilder().build();
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
            CodeLinkLookupResponse response = this.connect();
            if (response == null) {
                return;
            }
            this.sessionId = response.sessionId;
            if (response.timedOut) {
                return;
            }
            long sid = this.sessionId;
            this.ide.jumpToClass(response.className, response.methodName, (b) -> {
                try {
                    this.respond(b ? RESPONSE_FOUND : RESPONSE_NOT_FOUND, sid);
                } catch (CodeLinkResponseException | CodeLinkConnectionException e) {
                    CodeLinkClient.LOGGER.warning("Could not send response to CodeLink: " + e.getMessage());
                }
            });

            this.hasErrored = false;
        } catch (CodeLinkConnectionException e) {
            if (e.getCause() instanceof UnknownHostException) {
                this.clSettings.setEnabled(false);
                this.ide.log(Level.SEVERE, "CodeLink Error", "Could not connect to client.", "CodeLink has been disabled<br><b>Check your configuration</b>", true);
            } else if (!hasErrored) {
                //this.ide.log(Level.SEVERE, "CodeLink Error", "Could not connect to client.", "<b>Check your configuration</b>", true);
                //this.hasErrored = true;
            }
        } catch (Exception e) {
            if (!hasErrored) {
                this.ide.log(Level.SEVERE, "CodeLink Error", "Could not connect to client.", "<b>Check your configuration</b>", true);
            }
            this.hasErrored = true;
            //skip 5 connections
            this.suppress = 5;
        }
    }

    @Nullable
    private CodeLinkLookupResponse connect() throws CodeLinkConnectionException, CodeLinkResponseException {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("ideid", String.valueOf(this.ide.getId())));
        nvps.add(new BasicNameValuePair("ideversion", this.ide.getVersion()));
        nvps.add(new BasicNameValuePair("major", this.ide.getPluginVersion().major));
        nvps.add(new BasicNameValuePair("minor", this.ide.getPluginVersion().minor));
        nvps.add(new BasicNameValuePair("revision", this.ide.getPluginVersion().revision));
        nvps.add(new BasicNameValuePair("sessionid", String.valueOf(this.sessionId)));
        nvps.add(new BasicNameValuePair("activeproject", this.ide.getProjectName()));
        nvps.add(new BasicNameValuePair("projectpath", this.ide.getProjectPath()));

        StringBuilder builder = PollingWorker.buildURL(this.clSettings).append("connect");
        HttpPost post = new HttpPost(builder.toString());
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps));
            try (CloseableHttpResponse response = this.client.execute(post)) {
                if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
                    throw new CodeLinkResponseException("Invalid status code: " + response.getStatusLine().getStatusCode());
                }
                return Utils.inputStreamToObject(response.getEntity().getContent(), CodeLinkLookupResponse.class);
            } catch (JAXBException e) {
                throw new CodeLinkResponseException(e);
            }
        } catch (IOException e) {
            throw new CodeLinkConnectionException(e);
        }
    }

    private void respond(int responseCode, long sessionId) throws CodeLinkConnectionException, CodeLinkResponseException {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("sessionid", String.valueOf(sessionId)));
        nvps.add(new BasicNameValuePair("responsecode", String.valueOf(responseCode)));

        StringBuilder builder = PollingWorker.buildURL(this.clSettings).append("response");
        HttpPost post = new HttpPost(builder.toString());
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps));
            try (CloseableHttpResponse response = this.client.execute(post)) {
                if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
                    throw new CodeLinkResponseException("Invalid status code: " + response.getStatusLine().getStatusCode());
                }
            }
        } catch (IOException e) {
            throw new CodeLinkConnectionException(e);
        }
    }
}
