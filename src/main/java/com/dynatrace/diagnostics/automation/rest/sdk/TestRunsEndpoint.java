package com.dynatrace.diagnostics.automation.rest.sdk;

import com.dynatrace.diagnostics.Utils;
import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestRun;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * TestRunsEndpoint is responsible for fetching TestRun summary
 * It does not respect changes in IServerSettings regarding credentials, so it is required to
 * create a new instance every time Credentials change.
 */
public class TestRunsEndpoint {
    private final CloseableHttpClient client;
    private final IServerSettings settings;

    public TestRunsEndpoint(IServerSettings settings) {
        this.settings = settings;
        HttpClientBuilder builder = Utils.clientBuilder();
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(settings.getLogin(), settings.getPassword()));
        builder.setDefaultCredentialsProvider(provider);
        this.client = builder.build();
    }

    public TestRun getTestRun(String profileName, String testId) throws IOException, JAXBException {
        // use RESTlib URL builder
        ManagementURLBuilder builder = new ManagementURLBuilder();
        builder.setServerAddress((settings.isSSL() ? "https://" : "http://") + settings.getHost() + ":" + settings.getPort() + "/");
        URL url = builder.getTestrunsURL(profileName);
        // append test run id to the url
        String stringURL = url.toString() + "/" + testId + ".xml";

        // do the request
        HttpGet request = new HttpGet(stringURL);
        try (CloseableHttpResponse response = this.client.execute(request)) {
            String content = EntityUtils.toString(response.getEntity());
            System.out.println(content);
            InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            TestRun testRun = Utils.inputStreamToObject(stream, TestRun.class);
            return testRun;
        }
    }
}
