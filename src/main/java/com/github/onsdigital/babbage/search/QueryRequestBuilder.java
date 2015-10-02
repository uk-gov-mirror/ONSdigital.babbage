package com.github.onsdigital.babbage.search;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.List;

/**
 * Created by bren on 17/09/15.
 * <p/>
 * Converts ONSQuery wrapper to elastic search query builder
 */
class QueryRequestBuilder {

    SearchRequestBuilder buildSearchRequest(SearchRequestBuilder builder, ONSQuery query) {
        builder.setQuery(buildSearchQuery(query))
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setTypes(query.getTypes());
        if (query.getFrom() != null) {
            builder.setFrom(query.getFrom());
        }
        if (query.getSize() != null) {
            builder.setSize(query.getSize());
        }
        if (query.isHighLightFields()) {
            setHighlights(builder, query.getFields());
        }
        addSorts(builder, query.getSorts());
        return builder;
    }

    public CountRequestBuilder buildCountRequest(CountRequestBuilder builder, ONSQuery query) {
        builder.setQuery(buildSearchQuery(query))
                .setTypes(query.getTypes());
        return builder;
    }

    private QueryBuilder buildSearchQuery(ONSQuery query) {
        QueryBuilder queryBuilder = buildQuery(query);
        FilterBuilder filterBuilder = buildFilter(query);
        return new FilteredQueryBuilder(queryBuilder, filterBuilder);
    }

    private QueryBuilder buildQuery(ONSQuery query) {
        if (StringUtils.isEmpty(query.getSearchTerm())) {
            return null;
        }
        return new MultiMatchQueryBuilder(query.getSearchTerm(), query.getBoostedFields());
    }

    private FilterBuilder buildFilter(ONSQuery query) {
        AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter();
        List<FilterBuilder> filters = query.getFilters();
        if (filters.isEmpty()) {
            return null;
        }
        for (FilterBuilder filter : filters) {
            andFilterBuilder.add(filter);
        }
        return andFilterBuilder;
    }

    private void setHighlights(SearchRequestBuilder searchRequestBuilder,  String... fields) {
        if (fields == null) {
            return;
        }
        for (String field : fields) {
            searchRequestBuilder.addHighlightedField(field, 0, 0);
        }
        searchRequestBuilder.setHighlighterPreTags(ONSQuery.HIGHLIGHTER_PRE_TAG);
        searchRequestBuilder.setHighlighterPostTags(ONSQuery.HIGHLIGHTER_POST_TAG);
    }

    private void addSorts(SearchRequestBuilder searchRequestBuilder, List<SortBuilder> sorts) {
        for (SortBuilder sort : sorts) {
            searchRequestBuilder.addSort(sort);
        }
    }


}
