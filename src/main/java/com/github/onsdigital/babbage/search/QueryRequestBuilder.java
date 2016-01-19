//package com.github.onsdigital.babbage.search;
//
//import com.github.onsdigital.babbage.search.model.field.HighlightField;
//import org.apache.commons.lang3.StringUtils;
//import org.elasticsearch.action.count.CountRequestBuilder;
//import org.elasticsearch.action.search.SearchRequestBuilder;
//import org.elasticsearch.action.search.SearchType;
//import org.elasticsearch.index.query.*;
//import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
//import org.elasticsearch.search.aggregations.AggregationBuilder;
//import org.elasticsearch.search.sort.SortBuilder;
//
//import java.util.List;
//
///**
// * Created by bren on 17/09/15.
// * <p/>
// * Converts ONSQuery wrapper to elastic search query builder
// */
//class QueryRequestBuilder {
//
//    SearchRequestBuilder buildSearchRequest(SearchRequestBuilder builder, ONSQuery query) {
//        builder.setQuery(buildSearchQuery(query))
//                .setTypes(query.getTypes());
//
//        if (query instanceof AggregateQuery) {
//            builder.setSearchType(SearchType.COUNT);
//        } else {
//            //checkout https://www.elastic.co/blog/understanding-query-then-fetch-vs-dfs-query-then-fetch
//            //we don't want shards to affect the results as latest results sometimes become less relevant than earlier ones
//            builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
//        }
//        if (query.getFrom() != null) {
//            builder.setFrom(query.getFrom());
//        }
//        if (query.getSize() != null) {
//            builder.setSize(query.getSize());
//        }
//        if (query.isHighLightFields()) {
//            setHighlights(builder, HighlightField.values());
//        }
//        addSorts(builder, query.getSorts());
//        addAggregations(builder, query.getAggregations());
//        return builder;
//    }
//
//    public CountRequestBuilder buildCountRequest(CountRequestBuilder builder, ONSQuery query) {
//        builder.setQuery(buildSearchQuery(query))
//                .setTypes(query.getTypes());
//        return builder;
//    }
//
//    private QueryBuilder buildSearchQuery(ONSQuery query) {
//        QueryBuilder queryBuilder = buildQuery(query);
//        FilterBuilder filterBuilder = buildFilter(query);
//        return new FilteredQueryBuilder(queryBuilder, filterBuilder);
//    }
//
//    private QueryBuilder buildQuery(ONSQuery query) {
//        if (StringUtils.isEmpty(query.getSearchTerm())) {
//            return null;
//        }
//        return new MultiMatchQueryBuilder(query.getSearchTerm(), query.getBoostedFields());
//    }
//
//    private FilterBuilder buildFilter(ONSQuery query) {
//        AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter();
//        List<FilterBuilder> filters = query.getFilters();
//        if (filters.isEmpty()) {
//            return null;
//        }
//        for (FilterBuilder filter : filters) {
//            andFilterBuilder.add(filter);
//        }
//        return andFilterBuilder;
//    }
//
//    private void setHighlights(SearchRequestBuilder searchRequestBuilder,  HighlightField[] highlightFields) {
//        if (highlightFields == null) {
//            return;
//        }
//        for (HighlightField field : highlightFields) {
//            searchRequestBuilder.addHighlightedField(field.name(), 0, 0);
//        }
//        searchRequestBuilder.setHighlighterPreTags(ONSQuery.HIGHLIGHTER_PRE_TAG);
//        searchRequestBuilder.setHighlighterPostTags(ONSQuery.HIGHLIGHTER_POST_TAG);
//    }
//
//    private void addSorts(SearchRequestBuilder searchRequestBuilder, List<SortBuilder> sorts) {
//        for (SortBuilder sort : sorts) {
//            searchRequestBuilder.addSort(sort);
//        }
//    }
//
//    private void addAggregations(SearchRequestBuilder builder, List<AggregationBuilder> aggregations) {
//        for (AggregationBuilder aggregation : aggregations) {
//            builder.addAggregation(aggregation);
//        }
//    }
//
//
//
//}
