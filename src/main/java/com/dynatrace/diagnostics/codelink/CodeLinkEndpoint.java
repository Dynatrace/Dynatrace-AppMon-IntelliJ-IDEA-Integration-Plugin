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

package com.dynatrace.diagnostics.codelink;

import com.dynatrace.diagnostics.Utils;
import com.dynatrace.diagnostics.codelink.exceptions.CodeLinkConnectionException;
import com.dynatrace.diagnostics.codelink.exceptions.CodeLinkResponseException;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for making requests to Dynatrace client polling for CodeLink (lookup) requests.
 * {@link #connect(long) connect} method should be called frequently to minimize delays.
 */
public class CodeLinkEndpoint {
    public static final ClientVersion DTCLIENT_VERSION_WITH_INTELLIJ_SUPPORT = new ClientVersion(8, 0, 0, 0);

    private final IProjectDescriptor project;
    private final IIDEDescriptor ide;
    private final ICodeLinkSettings clSettings;
    private final CloseableHttpClient client;
    private ClientVersion version;

    public CodeLinkEndpoint(IProjectDescriptor project, IIDEDescriptor ide, ICodeLinkSettings settings) {
        this.project = project;
        this.ide = ide;
        this.clSettings = settings;
        this.client = Utils.clientBuilder().build();
    }

    private static StringBuilder buildURL(ICodeLinkSettings settings) {
        return new StringBuilder(settings.isSSL() ? "https://" : "http://")
                .append(settings.getHost())
                .append(':').append(settings.getPort())
                .append("/rest/management/");
    }

    /**
     * Makes a request to the Dynatrace client polling for CodeLink requests
     * If CodeLinkLookupResponse.timedOut is true, there is no pending request,
     * otherwise className and methodName should be populated.
     * After receiving a CodeLink request one should call {@link #respond(ResponseStatus, long) respond} method.
     *
     * @param sessionId - id returned previously by this method or -1 if it's the first request
     * @return A {@link CodeLinkLookupResponse response} containing CodeLink request data.
     */
    @NotNull
    public CodeLinkLookupResponse connect(long sessionId) throws CodeLinkConnectionException, CodeLinkResponseException {
        try {
            if (this.version == null) {
                this.version = this.getClientVersion();
            }
            //0 stands for eclipse IDE, older versions of dynatrace have no support for IDEA, therefore we disguise under eclipse
            int ideId = this.version.compareTo(DTCLIENT_VERSION_WITH_INTELLIJ_SUPPORT) < 0 ? 0 : this.ide.getId();

            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("ideid", String.valueOf(ideId)));
            nvps.add(new BasicNameValuePair("ideversion", this.ide.getVersion()));
            nvps.add(new BasicNameValuePair("major", this.ide.getPluginVersion().major));
            nvps.add(new BasicNameValuePair("minor", this.ide.getPluginVersion().minor));
            nvps.add(new BasicNameValuePair("revision", this.ide.getPluginVersion().revision));
            nvps.add(new BasicNameValuePair("sessionid", String.valueOf(sessionId)));
            nvps.add(new BasicNameValuePair("activeproject", this.project.getProjectName()));
            nvps.add(new BasicNameValuePair("projectpath", this.project.getProjectPath()));

            StringBuilder builder = CodeLinkEndpoint.buildURL(this.clSettings).append("codelink/connect");
            HttpPost post = new HttpPost(builder.toString());

            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setEntity(new UrlEncodedFormEntity(nvps));
            try (CloseableHttpResponse response = this.client.execute(post)) {
                if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
                    throw new CodeLinkConnectionException(response.getStatusLine().getReasonPhrase());
                }
//                String content = EntityUtils.toString(response.getEntity());
//                System.out.println(content);
//                InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
                return Utils.inputStreamToObject(response.getEntity().getContent(), CodeLinkLookupResponse.class);
            } catch (JAXBException e) {
                throw new CodeLinkResponseException(e);
            }
        } catch (IOException e) {
            throw new CodeLinkConnectionException(e);
        }
    }

    /**
     * Makes a request to Dynatrace client informing it about a state of CodeLink request.
     *
     * @param responseCode - states whether caller has successfully navigated to the source-code
     * @param sessionId    - an id returned by {@link #connect(long) connect} method when getting lookup request.
     * @throws CodeLinkConnectionException if connection is not established or status code is invalid.
     */
    public void respond(ResponseStatus responseCode, long sessionId) throws CodeLinkConnectionException {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("sessionid", String.valueOf(sessionId)));
        nvps.add(new BasicNameValuePair("responsecode", String.valueOf(responseCode.code)));

        StringBuilder builder = CodeLinkEndpoint.buildURL(this.clSettings).append("codelink/response");
        HttpPost post = new HttpPost(builder.toString());
        try {
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
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

    public ClientVersion getClientVersion() throws CodeLinkConnectionException, CodeLinkResponseException {
        StringBuilder builder = CodeLinkEndpoint.buildURL(this.clSettings).append("version");
        HttpGet get = new HttpGet(builder.toString());
        try (CloseableHttpResponse response = this.client.execute(get)) {
            if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
                throw new CodeLinkConnectionException(response.getStatusLine().getReasonPhrase());
            }
            XPath xPath = XPathFactory.newInstance().newXPath();
            return ClientVersion.fromString(xPath.compile("/result/@value").evaluate(new InputSource(response.getEntity().getContent())));
        } catch (IOException e) {
            throw new CodeLinkConnectionException(e);
        } catch (Exception e) {
            throw new CodeLinkResponseException(e);
        }
    }

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
}
