package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.util.http.BabbageHttpClient;
import com.github.onsdigital.babbage.util.http.ClientConfiguration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

import static com.github.onsdigital.babbage.configuration.Configuration.CONTENT_SERVICE.getMaxContentServiceConnection;

public class SearchHttpClient extends BabbageHttpClient {

    SearchHttpClient() {
        super(Configuration.SEARCH_SERVICE.getExternalSearchAddress(), createConfiguration());
    }

    public CloseableHttpResponse execute(HttpRequestBase requestBase) throws IOException {
        return this.httpClient.execute(requestBase);
    }

    private static ClientConfiguration createConfiguration() {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxTotalConnection(getMaxContentServiceConnection());
        configuration.setDisableRedirectHandling(true);
        return configuration;
    }

}
