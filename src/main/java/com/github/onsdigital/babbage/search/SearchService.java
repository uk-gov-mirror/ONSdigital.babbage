package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.search.helpers.CountResponseHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
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
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;

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

    public SearchResponseHelper search(ONSQueryBuilder queryBuilder) throws IOException {
        SearchRequestBuilder searchRequestBuilder = buildSearch(queryBuilder);
        return new SearchResponseHelper(searchRequestBuilder.get());
    }

    public List<SearchResponseHelper> multipleSearch(ONSQueryBuilder... queryBuilders) throws IOException {
        List<SearchResponseHelper> helpers = new ArrayList<>();
        MultiSearchRequestBuilder multiSearchRequestBuilder = client.prepareMultiSearch();
        for (ONSQueryBuilder queryBuilder : queryBuilders) {
            multiSearchRequestBuilder.add(buildSearch(queryBuilder));
        }
        MultiSearchResponse response = multiSearchRequestBuilder.get();
        for (MultiSearchResponse.Item item : response.getResponses()) {
            if (item.isFailure()) {
                throw new ElasticsearchException(item.getFailureMessage());
            }
            helpers.add(new SearchResponseHelper(item.getResponse()));
        }
        return helpers;
    }

    private SearchRequestBuilder buildSearch(ONSQueryBuilder queryBuilder) {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(getElasticSearchIndexAlias()).setQuery(queryBuilder.build());

        if (queryBuilder.getFrom() != null) {
            searchRequestBuilder.setFrom(queryBuilder.getFrom()).setSize(queryBuilder.getSize());
        }
        String[] types = queryBuilder.getTypes();
        if (types != null) {
            searchRequestBuilder.setTypes(types);
        }
        if (queryBuilder.isHighLightFields()) {
            setHighlights(searchRequestBuilder,queryBuilder.getFields());
        }
        addSorts(queryBuilder.getSorts(), searchRequestBuilder);
        searchRequestBuilder.addSort(new ScoreSortBuilder()); //sort by score last
        System.out.println("Searching with query:\n" + searchRequestBuilder.internalBuilder());
        return searchRequestBuilder;
    }

    private void setHighlights(SearchRequestBuilder searchRequestBuilder, String... fields) {
        if (fields == null) {
            return;
        }
        for (String field : fields) {
            searchRequestBuilder.addHighlightedField(field);
        }
        searchRequestBuilder.setHighlighterPreTags(ONSQueryBuilder.HIGHLIGHTER_PRE_TAG);
        searchRequestBuilder.setHighlighterPostTags(ONSQueryBuilder.HIGHLIGHTER_POST_TAG);
        searchRequestBuilder.setHighlighterForceSource(true);
    }

    public CountResponseHelper count(ONSQueryBuilder queryBuilder) {
        CountRequestBuilder countRequestBuilder = client.prepareCount(getElasticSearchIndexAlias()).setQuery(queryBuilder.build());
        String[] types = queryBuilder.getTypes();
        if (types != null) {
            countRequestBuilder.setTypes(types);
        }
        return new CountResponseHelper(countRequestBuilder.get());
    }

    private void addSorts(List<SortBuilder> sorts, SearchRequestBuilder searchRequestBuilder) {
        for (SortBuilder sort : sorts) {
            searchRequestBuilder.addSort(sort);
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
