package com.github.onsdigital.babbage.search;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetSocketAddress;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.babbage.logging.LogEvent.logEvent;

/**
 * Created by bren on 16/12/15.
 */
public class ElasticSearchClient {

    private static Client client;

    private ElasticSearchClient() {

    }

    public static Client getElasticsearchClient() {
        return client;
    }

    public static void init() {
        logEvent().debug("Initialising Elasticsearch client");
        if (client == null) {
            initTransportClient();
        }
    }

    private static void initTransportClient() {
        logEvent().debug("Using Elasticsearch transport client");

        Settings.Builder builder = Settings.builder();

        String clusterName = appConfig().elasticSearch().cluster();
        if (!StringUtils.isBlank(clusterName))
            builder.put("cluster.name", clusterName);

        logEvent()
                .parameter("host", appConfig().elasticSearch().host())
                .parameter("port", appConfig().elasticSearch().port())
                .debug("Attempting to connect to Elasticsearch cluster");

        Settings settings = builder.build();
        client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(
                        new InetSocketAddress(
                                appConfig().elasticSearch().host(),
                                appConfig().elasticSearch().port())));
    }

}
