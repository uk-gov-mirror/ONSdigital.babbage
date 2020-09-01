package com.github.onsdigital.babbage.highcharts;

import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.util.http.ClientConfiguration;
import com.github.onsdigital.babbage.util.http.PooledHttpClient;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

/**
 * Created by bren on 17/06/15.
 */
public class HighChartsExportClient {


    private static PooledHttpClient client;
    private static HighChartsExportClient instance;
    private static Integer count;
    private static HashMap<String, byte[]> cache;

    // singleton
    private HighChartsExportClient() {
    }

    public static HighChartsExportClient getInstance() {
        if (instance == null) {
            synchronized (HighChartsExportClient.class) {
                if (instance == null) {
                    instance = new HighChartsExportClient();
                    info().log("initializing Highcharts export server client connection pool");
                    client = new PooledHttpClient(appConfig().babbage().getExportSeverUrl(), createConfiguration());
                    // client = new PooledHttpClient("https://www.google.com", createConfiguration());
                    count = 0;
                    cache = new HashMap<>();
                }
            }
        }
        return instance;
    }

    private static ClientConfiguration createConfiguration() {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxTotalConnection(appConfig().babbage().getMaxHighchartsServerConnections());
        return configuration;
    }

    public InputStream getImage(String chartConfig, Integer width) throws IOException {
        return getImage(chartConfig, width, null);
    }

    /**
     * Retrived the image from Highcharts. <b>Caller is responsible for closing the returned {@link InputStream}</b>
     */
    public InputStream getImage(String chartConfig, Integer width, Double scale) throws IOException {
        String cacheKey = chartConfig + "-" + (width == null ? "0" : width.toString()) + "-" + (scale == null ? "0" : scale.toString());
        if (cache.containsKey(cacheKey)) {
            info().log("Returning cached value");
            return new ByteArrayInputStream(cache.get(cacheKey));
        }
        info().log("making request to Highcharts export server");
        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("options", chartConfig));
        postParameters.add(new BasicNameValuePair("type", "png"));
        if (width != null) {
            postParameters.add(new BasicNameValuePair("width", width.toString()));
        }
        if (scale != null) {
            postParameters.add(new BasicNameValuePair("scale", scale.toString()));
        }

        try (CloseableHttpResponse response = client.sendPost("/", null, postParameters)) {
        // try (CloseableHttpResponse response = client.sendGet("/logos/doodles/2020/celebrating-dr-harold-moody-6753651837108573.2-l.png", null, null)) {
            // try with resources block will close the response InputStream when the method returns.
            // take a copy of the bytes and return a new Inputstream which is the callers responsibility to close.
            byte[] content = IOUtils.toByteArray(response.getEntity().getContent());
            cache.put(cacheKey, content);
            return new ByteArrayInputStream(content);
        } catch (IOException ex) {
            throw error().logException(new ResourceNotFoundException(ex.getMessage()),
                    "Unexpected error while requesting highcharts image");
        }
    }

}
