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

package com.dynatrace.diagnostics.automation.rest.sdk;

import com.dynatrace.diagnostics.Utils;
import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestRun;
import com.dynatrace.diagnostics.automation.rest.sdk.exceptions.TestRunsConnectionException;
import com.dynatrace.diagnostics.automation.rest.sdk.exceptions.TestRunsResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;

/**
 * TestRunsEndpoint is responsible for fetching TestRun summary
 */
public class TestRunsEndpoint {
    private final CloseableHttpClient client;
    private final ServerSettings settings;

    public TestRunsEndpoint(@NotNull ServerSettings settings) {
        this.settings = settings;
        HttpClientBuilder builder = Utils.clientBuilder();
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, this.settings);
        builder.setDefaultCredentialsProvider(provider);
        this.client = builder.build();
    }

    /**
     * Fetches TestRun with a given testId and profileName from the dynatrace server.
     *
     * @param profileName - a profileName testId belongs to
     * @param testId      - a deterministic ID provided you during test registration
     * @return TestRun object containing basic information and metrics about test runs.
     */
    @NotNull
    public TestRun getTestRun(@NotNull String profileName, @NotNull String testId) throws TestRunsConnectionException, TestRunsResponseException {
        // use RESTlib URL builder
        ManagementURLBuilder builder = new ManagementURLBuilder();
        builder.setServerAddress((settings.isSSL() ? "https://" : "http://") + settings.getHost() + ":" + settings.getPort());
        URL url = builder.getTestrunsURL(profileName);
        // append test run id to the url
        String stringURL = url.toString() + "/" + testId + ".xml";

        // do the request
        HttpGet request = new HttpGet(stringURL);
        try (CloseableHttpResponse response = this.client.execute(request)) {
            if (response.getStatusLine().getStatusCode() >= 300 || response.getStatusLine().getStatusCode() < 200) {
                throw new TestRunsConnectionException(response.getStatusLine().getReasonPhrase());
            }
            try {
                return Utils.inputStreamToObject(response.getEntity().getContent(), TestRun.class);
            } catch (JAXBException | IOException e) {
                throw new TestRunsResponseException(e);
            }
        } catch (IOException e) {
            throw new TestRunsConnectionException(e);
        }
    }
}
