package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.search.model.sort.SortField;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.getElasticSearchIndexAlias;
import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getMaxVisiblePaginatorLink;
import static com.github.onsdigital.babbage.search.ElasticSearchClient.getElasticsearchClient;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class SearchHelper {

    // List of retired urls to be temporarily black-listed until content is cleared up (Trello card #482)
    private static List<String> retiredUrls = Configuration.ELASTIC_SEARCH.getHighlightBlacklist();

    private static SearchRequestBuilder prepare(ONSQuery query) {
        return prepare(query, null);
    }

    private static SearchRequestBuilder prepare(ONSQuery query, String index) {
        SearchRequestBuilder requestBuilder = getElasticsearchClient()
                .prepareSearch(isNotEmpty(index) ? index : getElasticSearchIndexAlias())
                .setQuery(query.query())
                .setFrom(query.from())
                .setSize(query.size()).setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        addTypes(requestBuilder, query);
        addHighlights(requestBuilder, query);
        addSorts(requestBuilder, query);
        addAggregations(requestBuilder, query);
        addSuggestions(requestBuilder, query);
        addFetchFields(requestBuilder, query);

        return requestBuilder;
    }

    public static ONSSearchResponse search(ONSQuery queries, String index) {
        SearchResponse response = prepare(queries, index).get();
        return resolveDetails(queries, new ONSSearchResponse(response));
    }

    public static ONSSearchResponse search(ONSQuery queries) {
        SearchRequestBuilder searchRequestBuilder = prepare(queries);
        //System.out.println("Searching with query:\n" + searchRequestBuilder.internalBuilder());
        return resolveDetails(queries, new ONSSearchResponse(searchRequestBuilder.get()));
    }

    public static List<ONSSearchResponse> searchMultiple(List<ONSQuery> queries) {
        MultiSearchRequestBuilder multiSearchRequestBuilder = getElasticsearchClient().prepareMultiSearch();
        for (ONSQuery builder : queries) {
            SearchRequestBuilder searchRequestBuilder = prepare(builder);
            //System.out.println("Searching with query:\n" + searchRequestBuilder.internalBuilder());
            multiSearchRequestBuilder.add(searchRequestBuilder);
        }

        List<ONSSearchResponse> helpers = new ArrayList<>();
        MultiSearchResponse response = multiSearchRequestBuilder.get();
        {
            int i = 0;
            for (MultiSearchResponse.Item item : response.getResponses()) {
                if (item.isFailure()) {
                    throw new ElasticsearchException(item.getFailureMessage());
                }
                ONSQuery query = queries.get(i);
                ONSSearchResponse searchResponse = resolveDetails(query, new ONSSearchResponse(item.getResponse()));

                List<ContentType> types = Arrays.asList(query.types());

                // Check for retired product pages
                if (query.size() == 1 &&
                        types.contains(ContentType.product_page)) {
                    // Get the hit - we only queried for 1 document so we should only get 1 back
                    SearchHits searchHits = searchResponse.response.getHits();
                    if (searchHits.getHits().length == 1) {
                        SearchHit searchHit = searchHits.getAt(0);
                        // ID is the url
                        String url = searchHit.getId();
                        if (!retiredUrls.contains(url)) {
                            // OK to add response
                            helpers.add(searchResponse);
                            i++;
                        }
                    }
                } else {
                    // Not a topic match query, so go ahead and add the response as usual
                    helpers.add(searchResponse);
                    i++;
                }
            }
        }
        return helpers;
    }


    private static void addTypes(SearchRequestBuilder builder, ONSQuery query) {
        if (query.types() == null) {
            return;
        }
        builder.setTypes(ContentType.typeNames(query.types()));
    }

    private static void addFetchFields(SearchRequestBuilder builder, ONSQuery query) {
        if (query.fetchFields() == null) {
            return;
        }
        builder.setFetchSource(Field.fieldNames(query.fetchFields()), null);
    }

    private static void addHighlights(SearchRequestBuilder builder, ONSQuery query) {
        if (!query.highlight()) {
            return;
        }
        builder.setHighlighterPreTags("<strong>");
        builder.setHighlighterPostTags("</strong>");
        for (String fieldName : Field.highlightedFieldNames()) {
            builder.addHighlightedField(fieldName, 0, 0);
        }
    }

    private static void addSorts(SearchRequestBuilder builder, ONSQuery query) {
        if (query.sortBy() == null) {
            return;
        }
        for (SortField sortField : query.sortBy().getSortFields()) {
            builder.addSort(sortField.getField().fieldName(), sortField.getOrder());
        }
    }

    private static void addAggregations(SearchRequestBuilder builder, ONSQuery query) {
        if (query.aggregate() == null) {
            return;
        }
        for (AbstractAggregationBuilder aggregate : query.aggregate()) {
            builder.addAggregation(aggregate);
        }
    }

    private static void addSuggestions(SearchRequestBuilder requestBuilder, ONSQuery query) {
        if (query.suggest() == null) {
            return;
        }
        for (SuggestBuilder.SuggestionBuilder suggestion : query.suggest()) {
            requestBuilder.addSuggestion(suggestion);
        }
    }

    private static ONSSearchResponse resolveDetails(ONSQuery queryBuilder, ONSSearchResponse response) {
        return resolveSortBy(queryBuilder, resolvePaginator(queryBuilder, response));
    }

    private static ONSSearchResponse resolveSortBy(ONSQuery queryBuilder, ONSSearchResponse response) {
        if (queryBuilder.sortBy() == null) {
            return response;
        }
        response.getResult().setSortBy(queryBuilder.sortBy().name());
        return response;
    }

    private static ONSSearchResponse resolvePaginator(ONSQuery queryBuilder, ONSSearchResponse response) {
        if (queryBuilder.page() == null) { // if page not set , don't resolve pagination
            return response;
        }
        Paginator.assertPage(queryBuilder.page(), response);
        Paginator paginator = new Paginator(response.getNumberOfResults(), getMaxVisiblePaginatorLink(), queryBuilder.page(), queryBuilder.size());
        if (paginator.getNumberOfPages() > 1) {
            response.getResult().setPaginator(paginator);
        }
        return response;
    }


}
