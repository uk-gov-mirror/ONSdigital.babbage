package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.util.http.BabbageHttpClient;
import com.github.onsdigital.babbage.util.http.ClientConfiguration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;

public class SearchHttpClient extends BabbageHttpClient {

    SearchHttpClient() {
        super(appConfig().externalSearch().address(), createConfiguration());
    }

    public CloseableHttpResponse execute(HttpRequestBase requestBase) throws IOException {
        return this.httpClient.execute(requestBase);
    }

    private static ClientConfiguration createConfiguration() {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxTotalConnection(appConfig().externalSearch().getMaxConnections());
        configuration.setDisableRedirectHandling(true);
        return configuration;
    }

}
