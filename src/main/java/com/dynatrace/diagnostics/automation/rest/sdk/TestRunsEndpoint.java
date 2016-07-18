package com.dynatrace.diagnostics.automation.rest.sdk;

import com.dynatrace.diagnostics.Utils;
import com.dynatrace.diagnostics.automation.rest.sdk.entity.TestRun;
import com.dynatrace.diagnostics.automation.rest.sdk.exceptions.TestRunsConnectionException;
import com.dynatrace.diagnostics.automation.rest.sdk.exceptions.TestRunsResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
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
 * It does not respect changes in IServerSettings regarding credentials, so it is required to
 * create a new instance every time Credentials change.
 */
public class TestRunsEndpoint {
    private final CloseableHttpClient client;
    private final IServerSettings settings;

    public TestRunsEndpoint(@NotNull IServerSettings settings) {
        this.settings = settings;
        HttpClientBuilder builder = Utils.clientBuilder();
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(settings.getLogin(), settings.getPassword()));
        builder.setDefaultCredentialsProvider(provider);
        this.client = builder.build();
    }

    /**
     * Fetches TestRun with a given testId and profileName from the dynatrace server.
     *
     * @param profileName - a profileName testId belongs to
     * @param testId      - a deterministic ID provided you during test registration
     * @return TestRun object containing basic information and metrics about test runs.
     * @throws IOException
     * @throws JAXBException
     */
    @NotNull
    public TestRun getTestRun(@NotNull String profileName, @NotNull String testId) throws TestRunsConnectionException, TestRunsResponseException {
        // use RESTlib URL builder
        ManagementURLBuilder builder = new ManagementURLBuilder();
        builder.setServerAddress((settings.isSSL() ? "https://" : "http://") + settings.getHost() + ":" + settings.getPort() + "/");
        URL url = builder.getTestrunsURL(profileName);
        // append test run id to the url
        String stringURL = url.toString() + "/" + testId + ".xml";

        // do the request
        HttpGet request = new HttpGet(stringURL);
        try (CloseableHttpResponse response = this.client.execute(request)) {
            if (response.getStatusLine().getStatusCode() >= 300 || response.getStatusLine().getStatusCode() < 200) {
                throw new TestRunsConnectionException(response.getStatusLine().getReasonPhrase());
            }
//            String content = EntityUtils.toString(response.getEntity());
//            System.out.println(content);
//            InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
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
