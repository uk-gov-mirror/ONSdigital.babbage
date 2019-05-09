package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.util.http.BabbageHttpClient;
import com.github.onsdigital.babbage.util.http.ClientConfiguration;
import com.github.onsdigital.logging.v2.event.HTTP;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

public class SearchHttpClient extends BabbageHttpClient {

    SearchHttpClient() {
        super(appConfig().externalSearch().address(), createConfiguration());
    }

    public CloseableHttpResponse execute(HttpRequestBase requestBase) throws IOException {
        info().beginHTTP(new HTTP()
                .setMethod(requestBase.getMethod())
                .setPath(requestBase.getURI().getPath())
                .setQuery(requestBase.getURI().getQuery())
                .setScheme(requestBase.getURI().getScheme())
                .setHost(requestBase.getURI().getHost())
                .setPort(requestBase.getURI().getPort()))
                .log("making search request");

        return this.httpClient.execute(requestBase);
    }

    private static ClientConfiguration createConfiguration() {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxTotalConnection(appConfig().externalSearch().getMaxConnections());
        configuration.setDisableRedirectHandling(true);
        return configuration;
    }

}
