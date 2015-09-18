package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.search.helpers.CountResponseHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.*;

/**
 * Created by bren on 07/09/15.
 */
public class SearchService {

    private Client client;
    private static SearchService instance = new SearchService();

    private SearchService() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", getElasticSearchCluster()).build();

        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(getElasticSearchServer(), getElasticSearchPort()));
        Runtime.getRuntime().addShutdownHook(new ShutDownNodeThread(client));
    }

    public static SearchService getInstance() {
        return instance;
    }

    public SearchResponseHelper search(ONSQuery query) throws IOException {
        SearchRequestBuilder searchRequestBuilder = new QueryRequestBuilder().buildSearchRequest(newSearchRequest(), query);
        System.out.println("Searching: \ntypes:\n" + ArrayUtils.toString(query.getTypes()) + " \nquery:\n" + searchRequestBuilder.internalBuilder());
        return new SearchResponseHelper(searchRequestBuilder.get());
    }

    public CountResponseHelper count(ONSQuery query) {
        CountRequestBuilder countRequestBuilder =
                client.prepareCount(getElasticSearchIndexAlias());
        countRequestBuilder = new QueryRequestBuilder().buildCountRequest(countRequestBuilder, query);
        return new CountResponseHelper(countRequestBuilder.get());
    }

    public List<SearchResponseHelper> searchMultiple(ONSQuery... queries) throws IOException {
        MultiSearchRequestBuilder multiSearchRequestBuilder = client.prepareMultiSearch();
        for (ONSQuery query : queries) {
            SearchRequestBuilder requestBuilder = new QueryRequestBuilder().buildSearchRequest(newSearchRequest(), query);
            System.out.println("Searching: \ntypes:\n" + ArrayUtils.toString(query.getTypes())+ " \nquery:\n" + requestBuilder.internalBuilder());
            multiSearchRequestBuilder.add(requestBuilder);
        }
        List<SearchResponseHelper> helpers = doSearchMultiple(multiSearchRequestBuilder);
        return helpers;
    }

    private List<SearchResponseHelper> doSearchMultiple(MultiSearchRequestBuilder multiSearchRequestBuilder) {
        List<SearchResponseHelper> helpers = new ArrayList<>();
        MultiSearchResponse response = multiSearchRequestBuilder.get();
        for (MultiSearchResponse.Item item : response.getResponses()) {
            if (item.isFailure()) {
                throw new ElasticsearchException(item.getFailureMessage());
            }
            helpers.add(new SearchResponseHelper(item.getResponse()));
        }
        return helpers;
    }


    private SearchRequestBuilder newSearchRequest() {
        return client.prepareSearch(getElasticSearchIndexAlias());
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
