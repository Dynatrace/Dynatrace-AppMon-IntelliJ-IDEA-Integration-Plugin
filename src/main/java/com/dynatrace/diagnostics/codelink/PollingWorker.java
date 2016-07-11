package com.dynatrace.diagnostics.codelink;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

class PollingWorker implements Runnable {
    public static final int RESPONSE_FOUND = 50;
    public static final int RESPONSE_NOT_FOUND = 51;

    private static StringBuilder buildURL(ICodeLinkSettings settings) {
        return new StringBuilder(settings.isSSL() ? "https://" : "http://")
                .append(settings.getHost())
                .append(':').append(settings.getPort())
                .append("/rest/management/codelink/");
    }

    /**
     * Builds a custom {@link org.apache.http.impl.client.CloseableHttpClient} for Dynatrace client connection.
     * TODO @see for dynatrace docs
     *
     * @return Configured {@link org.apache.http.impl.client.CloseableHttpClient}
     * @see <a href="http://stackoverflow.com/questions/9402653/apache-httpclient-4-1-and-newer-how-to-do-basic-authentication" />
     */
    private static CloseableHttpClient createClient() {
        try {
            HttpClientBuilder builder = HttpClients.custom();
            // trust all certs
            builder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
            builder.setSslcontext(sslContext);

            return builder.build();
        } catch (Exception e) {
            //impossible path
            throw new RuntimeException("Error occured while creating custom CloseableHTTPClient in AppMonServerConfiguration", e);
        }
    }

    private static <T> T inputStreamToObject(InputStream xml, Class<T> clazz) throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        //TODO: check if try with resources is applicable here
        try {
            T obj = (T) unmarshaller.unmarshal(xml);
            return obj;
        } finally {
            xml.close();
        }
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
        this.client = PollingWorker.createClient();
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
                } catch (IOException e) {
                    //TODO: properly log this
                    e.printStackTrace();
                }
                return;
            });

            this.hasErrored = false;
        } catch (UnknownHostException e) {
            //TODO: if the configuration window is opened, update it
            this.clSettings.setEnabled(false);
            this.ide.showNotification("CodeLink Error", "<b>Check your configuration</b>");
        } catch (Exception e) {
            if (!hasErrored) {
                this.ide.showNotification("CodeLink Error", "<b>Check your configuration</b><br>Could not connect to client.");
            }
            this.hasErrored = true;
            //skip 5 connections
            this.suppress = 5;
        }
    }

    @Nullable
    private CodeLinkLookupResponse connect() throws IOException, JAXBException {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("ideid", String.valueOf(this.ide.getId())));
        nvps.add(new BasicNameValuePair("ideversion", this.ide.getVersion()));
        //TODO : implement
        nvps.add(new BasicNameValuePair("major", "0"));
        nvps.add(new BasicNameValuePair("minor", "2"));
        nvps.add(new BasicNameValuePair("revision", "3"));
        nvps.add(new BasicNameValuePair("sessionid", Long.toString(this.sessionId)));
        nvps.add(new BasicNameValuePair("activeproject", this.ide.getProjectName()));
        nvps.add(new BasicNameValuePair("projectpath", this.ide.getProjectPath()));

        StringBuilder builder = PollingWorker.buildURL(this.clSettings).append("connect");
        HttpPost post = new HttpPost(builder.toString());

        post.setEntity(new UrlEncodedFormEntity(nvps));
        try (CloseableHttpResponse response = this.client.execute(post)) {
            CodeLinkLookupResponse lookup = inputStreamToObject(response.getEntity().getContent(), CodeLinkLookupResponse.class);
            return lookup;
        }
    }

    private void respond(int responseCode, long sessionId) throws IOException {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("sessionid", String.valueOf(sessionId)));
        nvps.add(new BasicNameValuePair("responsecode", String.valueOf(responseCode)));

        //TODO: not thread safe currently.
        StringBuilder builder = PollingWorker.buildURL(this.clSettings).append("response");
        HttpPost post = new HttpPost(builder.toString());

        post.setEntity(new UrlEncodedFormEntity(nvps));
        try (CloseableHttpResponse response = this.client.execute(post)) {
            //??
        }
    }
}
