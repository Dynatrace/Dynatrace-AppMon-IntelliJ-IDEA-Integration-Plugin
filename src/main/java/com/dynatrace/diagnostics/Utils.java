package com.dynatrace.diagnostics;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    /**
     * Builds a custom {@link org.apache.http.impl.client.CloseableHttpClient} for Dynatrace client connection.
     *
     * @return Configured {@link org.apache.http.impl.client.CloseableHttpClient}
     * @see <a href="http://stackoverflow.com/questions/9402653/apache-httpclient-4-1-and-newer-how-to-do-basic-authentication" />
     */
    public static HttpClientBuilder clientBuilder() {
        try {
            HttpClientBuilder builder = HttpClients.custom();
            // trust all certs
            builder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
            builder.setSslcontext(sslContext);

            return builder;
        } catch (Exception e) {
            //impossible path
            throw new RuntimeException("Error occured while creating custom CloseableHTTPClient in AppMonServerConfiguration", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T inputStreamToObject(InputStream xml, Class<T> clazz) throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try {
            return (T) unmarshaller.unmarshal(xml);
        } finally {
            xml.close();
        }
    }
}
