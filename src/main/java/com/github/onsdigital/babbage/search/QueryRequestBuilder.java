package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import com.github.onsdigital.babbage.search.model.filter.RangeFilter;
import com.github.onsdigital.babbage.search.model.filter.ValueFilter;
import com.github.onsdigital.babbage.search.model.sort.SortField;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.FieldSortBuilder;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by bren on 17/09/15.
 * <p/>
 * Converts ONSQuery wrapper to elastic search query builder
 */
class QueryRequestBuilder {

    SearchRequestBuilder buildSearchRequest(SearchRequestBuilder builder, ONSQuery query) {
        builder.setQuery(buildSearchQuery(query))
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setTypes(getTypeNames(query.getTypes()));
        if (query.getFrom() != null) {
            builder.setFrom(query.getFrom());
        }
        if(query.getSize() != null) {
            builder.setSize(query.getSize());
        }
        if (query.isHighLightFields()) {
            setHighlights(builder, query.getFieldNames());
        }
        addSorts(builder, query.getSorts());
        return builder;
    }

    public CountRequestBuilder buildCountRequest(CountRequestBuilder builder, ONSQuery query) {
        builder.setQuery(buildSearchQuery(query))
                .setTypes(getTypeNames(query.getTypes()));
        return builder;
    }

    private QueryBuilder buildSearchQuery(ONSQuery query) {
        QueryBuilder queryBuilder = buildQuery(query);
        FilterBuilder filterBuilder = buildFilter(query);
        return new FilteredQueryBuilder(queryBuilder, filterBuilder);
    }

    private QueryBuilder buildQuery(ONSQuery query) {
        if (StringUtils.isEmpty(query.getQuery())) {
            return null;
        }
        return new MultiMatchQueryBuilder(query.getQuery(), query.getBoostedFieldNames());
    }

    private FilterBuilder buildFilter(ONSQuery query) {
        AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter();
        buildUriPrefix(query, andFilterBuilder);
        buildFieldFilters(query, andFilterBuilder);
        buildRangeFilters(query, andFilterBuilder);
        return andFilterBuilder;
    }

    private void buildUriPrefix(ONSQuery query, AndFilterBuilder andFilterBuilder) {
        if (isNotEmpty(query.getUriPrefix())) {
            andFilterBuilder.add(FilterBuilders.prefixFilter(FilterableField.uri.name(), query.getUriPrefix()));
        }
    }

    private void buildFieldFilters(ONSQuery query, AndFilterBuilder andFilterBuilder) {
        List<ValueFilter> filters = query.getFilters();
        if (filters.isEmpty()) {
            return;
        }
        for (int i = 0; i < filters.size(); i++) {
            ValueFilter filter = filters.get(i);
            FilterBuilder termFilterBuilder = FilterBuilders.termFilter(filter.getField().name(), filter.getValues());
            andFilterBuilder.add(termFilterBuilder);
        }
    }

    private void buildRangeFilters(ONSQuery query, AndFilterBuilder andFilterBuilder) {
        List<RangeFilter> rangeFilters = query.getRangeFilters();
        if (rangeFilters.isEmpty()) {
            return;
        }
        for (int i = 0; i < rangeFilters.size(); i++) {
            RangeFilter rangeFilter = rangeFilters.get(i);
            if (rangeFilter.getFrom() == null && rangeFilter.getTo() == null) {
                continue;
            }
            RangeFilterBuilder rangeFilterBuilder = FilterBuilders.rangeFilter(rangeFilter.getField().name());
            if (rangeFilter.getFrom() != null) {
                rangeFilterBuilder.from(rangeFilter.getFrom());
            }
            if (rangeFilter.getTo() != null) {
                rangeFilterBuilder.to(rangeFilter.getTo());
            }
            andFilterBuilder.add(rangeFilterBuilder);
        }

    }


    private String[] getTypeNames(ContentType... types) {
        if (types == null) {
            return null;
        }
        String[] typeNames = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            ContentType type = types[i];
            typeNames[i] = type.name();
        }

        return typeNames;
    }


    private void setHighlights(SearchRequestBuilder searchRequestBuilder, String... fields) {
        if (fields == null) {
            return;
        }
        for (String field : fields) {
            searchRequestBuilder.addHighlightedField(field, 0, 0);
        }
        searchRequestBuilder.setHighlighterPreTags(ONSQuery.HIGHLIGHTER_PRE_TAG);
        searchRequestBuilder.setHighlighterPostTags(ONSQuery.HIGHLIGHTER_POST_TAG);
    }

    private void addSorts(SearchRequestBuilder searchRequestBuilder, List<SortBy> sorts) {
        for (SortBy sort : sorts) {
            for (SortField sortField : sort.getSortFields()) {
                FieldSortBuilder sortBuilder = new FieldSortBuilder(sortField.getField().name())
                        .order(org.elasticsearch.search.sort.SortOrder.valueOf(sortField.getOrder().name()))
                        .ignoreUnmapped(true);
                searchRequestBuilder.addSort(sortBuilder);
            }
        }
    }


}
