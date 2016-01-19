package com.github.onsdigital.babbage.search;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetSocketAddress;

import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.*;

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
        if (client == null) {
            Settings settings = Settings.builder()
                    .put("cluster.name", getElasticSearchCluster()).build();
            client = TransportClient.builder().settings(settings).build()
                    .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(getElasticSearchServer(), getElasticSearchPort())));
            Runtime.getRuntime().addShutdownHook(new ShutDownNodeThread(client));
        }
    }

    private static class ShutDownNodeThread extends Thread {
        private Client client;

        public ShutDownNodeThread(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            client.close();
        }
    }

}
