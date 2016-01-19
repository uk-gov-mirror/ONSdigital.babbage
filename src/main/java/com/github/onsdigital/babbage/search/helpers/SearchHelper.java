package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.search.model.sort.SortField;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.getElasticSearchIndexAlias;
import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getMaxVisiblePaginatorLink;
import static com.github.onsdigital.babbage.search.ElasticSearchClient.getElasticsearchClient;

public class SearchHelper {

    private static SearchRequestBuilder prepare(ONSQueryBuilder queryBuilder) {
        SearchRequestBuilder searchRequestBuilder = getElasticsearchClient()
                .prepareSearch(getElasticSearchIndexAlias())
                .setTypes(queryBuilder.types())
                .setQuery(queryBuilder.query())
                .setFrom(queryBuilder.from())
                .setSize(queryBuilder.size())
                .setFetchSource(queryBuilder.fetchFields(), null);

        if (queryBuilder.highlight()) {
            addHighlights(searchRequestBuilder);
        }
        if (queryBuilder.sortBy() != null) {
            addSorts(searchRequestBuilder, queryBuilder.sortBy());
        }

        if (queryBuilder.aggregate() != null) {
            addAggregations(searchRequestBuilder, queryBuilder.aggregate());
        }

        return searchRequestBuilder;
    }

    public static SearchResponseHelper search(ONSQueryBuilder queryBuilder) {
        SearchRequestBuilder searchRequestBuilder = prepare(queryBuilder);
        System.out.println("Searching with query:\n" + searchRequestBuilder.internalBuilder());
        return resolveDetails(queryBuilder, new SearchResponseHelper(searchRequestBuilder.get()));
    }

    public static List<SearchResponseHelper> searchMultiple(ONSQueryBuilder... queryBuilders) {
        MultiSearchRequestBuilder multiSearchRequestBuilder = getElasticsearchClient().prepareMultiSearch();
        for (ONSQueryBuilder builder : queryBuilders) {
            SearchRequestBuilder searchRequestBuilder = prepare(builder);
            System.out.println("Searching with query:\n" + searchRequestBuilder.internalBuilder());
            multiSearchRequestBuilder.add(searchRequestBuilder);
        }

        List<SearchResponseHelper> helpers = new ArrayList<>();
        MultiSearchResponse response = multiSearchRequestBuilder.get();
        {
            int i = 0;
            for (MultiSearchResponse.Item item : response.getResponses()) {
                if (item.isFailure()) {
                    throw new ElasticsearchException(item.getFailureMessage());
                }
                helpers.add(resolveDetails(queryBuilders[i], new SearchResponseHelper(item.getResponse())));
                i++;
            }
        }
        return helpers;
    }


    private static SearchRequestBuilder addHighlights(SearchRequestBuilder builder) {
        builder.setHighlighterPreTags("<strong>");
        builder.setHighlighterPostTags("</strong>");
        for (String fieldName : Field.highlightedFieldNames()) {
            builder.addHighlightedField(fieldName, 0, 0);
        }
        return builder;
    }

    private static SearchRequestBuilder addSorts(SearchRequestBuilder builder, SortBy sortBy) {
        for (SortField sortField : sortBy.getSortFields()) {
            builder.addSort(sortField.getField().fieldName(), sortField.getOrder());
        }
        return builder;
    }

    private static SearchRequestBuilder addAggregations(SearchRequestBuilder builder, AbstractAggregationBuilder... aggregates) {
        for (AbstractAggregationBuilder aggregate : aggregates) {
            builder.addAggregation(aggregate);
        }
        return builder;
    }

    private static SearchResponseHelper resolveDetails(ONSQueryBuilder queryBuilder, SearchResponseHelper response) {
        return resolveSortBy(queryBuilder, resolvePaginator(queryBuilder, response));
    }

    private static SearchResponseHelper resolveSortBy(ONSQueryBuilder queryBuilder, SearchResponseHelper response) {
        if (queryBuilder.sortBy() == null) {
            return response;
        }
        response.getResult().setSortBy(queryBuilder.sortBy().name());
        return response;
    }

    private static SearchResponseHelper resolvePaginator(ONSQueryBuilder queryBuilder, SearchResponseHelper response) {
        if (queryBuilder.page() == null) { // if page not set , don't resolve pagination
            return response;
        }
        Paginator.assertPage(queryBuilder.page(), response);
        response.getResult().setPaginator(new Paginator(response.getNumberOfResults(), getMaxVisiblePaginatorLink(), queryBuilder.page(), queryBuilder.size()));
        return response;
    }

}
