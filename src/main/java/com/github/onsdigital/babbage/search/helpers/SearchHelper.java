package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.search.model.sort.SortField;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.getElasticSearchIndexAlias;
import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getMaxVisiblePaginatorLink;
import static com.github.onsdigital.babbage.search.ElasticSearchClient.getElasticsearchClient;

public class SearchHelper {

    private static SearchRequestBuilder prepare(ONSQuery query) {
        SearchRequestBuilder requestBuilder = getElasticsearchClient()
                .prepareSearch(getElasticSearchIndexAlias())
                .setQuery(query.query())
                .setFrom(query.from())
                .setSize(query.size());

        addTypes(requestBuilder, query);
        addHighlights(requestBuilder, query);
        addSorts(requestBuilder, query);
        addAggregations(requestBuilder, query);
        addFetchFields(requestBuilder, query);

        return requestBuilder;
    }

    public static ONSSearchResponse search(ONSQuery queries) {
        SearchRequestBuilder searchRequestBuilder = prepare(queries);
        System.out.println("Searching with query:\n" + searchRequestBuilder.internalBuilder());
        return resolveDetails(queries, new ONSSearchResponse(searchRequestBuilder.get()));
    }

    public static List<ONSSearchResponse> searchMultiple(List<ONSQuery> queries) {
        MultiSearchRequestBuilder multiSearchRequestBuilder = getElasticsearchClient().prepareMultiSearch();
        for (ONSQuery builder : queries) {
            SearchRequestBuilder searchRequestBuilder = prepare(builder);
            System.out.println("Searching with query:\n" + searchRequestBuilder.internalBuilder());
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
                helpers.add(resolveDetails(queries.get(i), new ONSSearchResponse(item.getResponse())));
                i++;
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
