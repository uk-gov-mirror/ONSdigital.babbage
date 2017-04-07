package com.github.onsdigital.babbage.util;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;

/**
 * Created by bren on 02/09/15.
 */
public class ElasticSearchUtils {
    private Client client;

    public ElasticSearchUtils(Client client) {
        this.client = client;
    }

    public CreateIndexResponse createIndex(String index, Settings settings) throws IOException {
        System.out.println("Creating index " + index);
        CreateIndexRequestBuilder createIndexRequest = getIndicesClient().prepareCreate(index);
        createIndexRequest.setSettings(settings);
        return createIndexRequest.get();
    }



    public boolean isIndexAvailable(String index) {
        IndicesExistsResponse response = client.admin().indices().prepareExists(index).execute().actionGet();
        return response.isExists();
    }


    private IndicesAdminClient getIndicesClient() {
        return client.admin().indices();
    }



}

