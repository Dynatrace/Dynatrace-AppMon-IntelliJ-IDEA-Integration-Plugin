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
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CodeLinkEndpoint {
    public enum ResponseStatus {
        FOUND(50), NOT_FOUND(51);

        private final int code;

        ResponseStatus(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    private static StringBuilder buildURL(ICodeLinkSettings settings) {
        return new StringBuilder(settings.isSSL() ? "https://" : "http://")
                .append(settings.getHost())
                .append(':').append(settings.getPort())
                .append("/rest/management/codelink/");
    }

    private final IProjectDescriptor project;
    private final IIDEDescriptor ide;
    private final ICodeLinkSettings clSettings;
    private final CloseableHttpClient client;

    public CodeLinkEndpoint(IProjectDescriptor project, IIDEDescriptor ide, ICodeLinkSettings settings) {
        this.project = project;
        this.ide = ide;
        this.clSettings = settings;
        this.client = Utils.clientBuilder().build();
    }

    @NotNull
    public CodeLinkLookupResponse connect(long sessionId) throws CodeLinkConnectionException, CodeLinkResponseException {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("ideid", String.valueOf(this.ide.getId())));
        nvps.add(new BasicNameValuePair("ideversion", this.ide.getVersion()));
        nvps.add(new BasicNameValuePair("major", this.ide.getPluginVersion().major));
        nvps.add(new BasicNameValuePair("minor", this.ide.getPluginVersion().minor));
        nvps.add(new BasicNameValuePair("revision", this.ide.getPluginVersion().revision));
        nvps.add(new BasicNameValuePair("sessionid", String.valueOf(sessionId)));
        nvps.add(new BasicNameValuePair("activeproject", this.project.getProjectName()));
        nvps.add(new BasicNameValuePair("projectpath", this.project.getProjectPath()));

        StringBuilder builder = CodeLinkEndpoint.buildURL(this.clSettings).append("connect");
        HttpPost post = new HttpPost(builder.toString());
        try {
            post.setHeader("Content-Type", "text/xml");
            post.setEntity(new UrlEncodedFormEntity(nvps));
            try (CloseableHttpResponse response = this.client.execute(post)) {
                if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
                    throw new CodeLinkConnectionException(response.getStatusLine().getReasonPhrase());
                }
                return Utils.inputStreamToObject(response.getEntity().getContent(), CodeLinkLookupResponse.class);
            } catch (JAXBException e) {
                throw new CodeLinkResponseException(e);
            }
        } catch (IOException e) {
            throw new CodeLinkConnectionException(e);
        }
    }

    public void respond(ResponseStatus responseCode, long sessionId) throws CodeLinkConnectionException {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("sessionid", String.valueOf(sessionId)));
        nvps.add(new BasicNameValuePair("responsecode", String.valueOf(responseCode.code)));

        StringBuilder builder = CodeLinkEndpoint.buildURL(this.clSettings).append("response");
        HttpPost post = new HttpPost(builder.toString());
        try {
            post.setHeader("Content-Type", "text/xml");
            post.setEntity(new UrlEncodedFormEntity(nvps));
            try (CloseableHttpResponse response = this.client.execute(post)) {
                if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
                    throw new CodeLinkConnectionException(response.getStatusLine().getReasonPhrase());
                }
            }
        } catch (IOException e) {
            throw new CodeLinkConnectionException(e);
        }
    }
}
