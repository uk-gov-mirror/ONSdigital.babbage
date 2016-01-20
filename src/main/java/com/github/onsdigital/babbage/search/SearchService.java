package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.search.helpers.CountResponseHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.getElasticSearchIndexAlias;
import static com.github.onsdigital.babbage.search.ElasticSearchClient.getElasticsearchClient;

public class SearchService {

    private static SearchService instance = new SearchService();
    private SearchService() {
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
                getElasticsearchClient().prepareCount(getElasticSearchIndexAlias());
        countRequestBuilder = new QueryRequestBuilder().buildCountRequest(countRequestBuilder, query);
        return new CountResponseHelper(countRequestBuilder.get());
    }

    public List<SearchResponseHelper> searchMultiple(ONSQuery... queries) throws IOException {
        MultiSearchRequestBuilder multiSearchRequestBuilder = getElasticsearchClient().prepareMultiSearch();
        for (ONSQuery query : queries) {
            SearchRequestBuilder requestBuilder = new QueryRequestBuilder().buildSearchRequest(newSearchRequest(), query);
            System.out.println("Searching: \ntypes:\n" + ArrayUtils.toString(query.getTypes()) + " \nquery:\n" + requestBuilder.internalBuilder());
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
        return getElasticsearchClient().prepareSearch(getElasticSearchIndexAlias());
    }


}
