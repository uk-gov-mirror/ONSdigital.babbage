package com.github.onsdigital.babbage.search;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.getElasticSearchCluster;
import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.getElasticSearchServer;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Created by bren on 16/12/15.
 */
public class ElasticSearchClient {

    private static Client client;
    private static Path searchHome;

    private ElasticSearchClient() {

    }

    public static Client getElasticsearchClient() {
        return client;
    }

    public static void init() throws IOException {
        if (client == null) {
            searchHome = Files.createTempDirectory("babbage_search_client");
            Settings settings = Settings.builder().put("http.enabled", false)
                    .put("cluster.name", getElasticSearchCluster())
                    .put("discovery.zen.ping.multicast.enabled", true)
                    .put("path.home", searchHome).build();
            Node node =
                    nodeBuilder()
                            .settings(settings)
                            .client(true)
                            .node();

            client = node.client();
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
            try {
                Files.deleteIfExists(searchHome);
            } catch (IOException e) {
                System.err.println("Falied cleaning temporary search client directory");
                e.printStackTrace();
            }
        }
    }

}
