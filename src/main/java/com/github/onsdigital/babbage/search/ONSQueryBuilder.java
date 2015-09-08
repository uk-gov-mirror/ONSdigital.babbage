package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.search.query.SortOrder;
import com.github.onsdigital.babbage.search.query.Type;
import com.github.onsdigital.babbage.search.query.filter.FieldFilter;
import com.github.onsdigital.babbage.search.query.filter.RangeFilter;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by bren on 07/09/15.
 */
public class ONSQueryBuilder {

    static final String ALL_FIELDS = "_all";
    static final String HIGHLIGHTER_PRE_TAG = "<strong>";
    static final String HIGHLIGHTER_POST_TAG = "</strong>";

    private Type[] types;
    private String uriPrefix;
    private String query;
    private List<FieldFilter> fieldFilters = new ArrayList<>();
    private List<RangeFilter> rangeFilters = new ArrayList<>();
    private boolean highLightFields;
    private List<SortBuilder> sorts = new ArrayList<>();
    private Integer page;
    private Integer size;

    public ONSQueryBuilder(Type... types) {
        this.types = types;
    }

    public Type[] getTypes() {
        return types;
    }

    public ONSQueryBuilder setTypes(Type... types) {
        this.types = types;
        return this;
    }

    public String getUriPrefix() {
        return uriPrefix;
    }

    public ONSQueryBuilder setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public ONSQueryBuilder setQuery(String query) {
        this.query = query;
        return this;
    }

    public List<FieldFilter> getFieldFilters() {
        return fieldFilters;
    }

    public ONSQueryBuilder addFilter(String field, String value) {
        fieldFilters.add(new FieldFilter(field, value));
        return this;
    }

    public List<RangeFilter> getRangeFilters() {
        return rangeFilters;
    }

    public ONSQueryBuilder addRangeFilter(String field, String from, String to) {
        rangeFilters.add(new RangeFilter(field, from, to));
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public ONSQueryBuilder setPage(int page) {
        this.page = page;
        return this;
    }


    QueryBuilder build() {
        QueryBuilder queryBuilder = buildQuery();
        FilterBuilder filterBuilder = buildFilter();
        return new FilteredQueryBuilder(queryBuilder, filterBuilder);
    }


    private QueryBuilder buildQuery() {
        if (StringUtils.isEmpty(getQuery())) {
            return null;
        }
        return new MatchQueryBuilder(ALL_FIELDS, getQuery()).analyzer(Configuration.ELASTIC_SEARCH.getSearchAnalyzer());
    }


    private FilterBuilder buildFilter() {
        AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter();
        buildUriPrefix(andFilterBuilder);
        buildFieldFilters(andFilterBuilder);
        buildRangeFilters(andFilterBuilder);
        return andFilterBuilder;
    }

    private void buildUriPrefix(AndFilterBuilder andFilterBuilder) {
        if (isNotEmpty(getUriPrefix())) {
            andFilterBuilder.add(FilterBuilders.prefixFilter("uri", getUriPrefix()));
        }
    }

    private void buildFieldFilters(AndFilterBuilder andFilterBuilder) {
        List<FieldFilter> fieldFilters = getFieldFilters();
        if (fieldFilters.isEmpty()) {
            return;
        }
        for (int i = 0; i < fieldFilters.size(); i++) {
            FieldFilter fieldFilter = fieldFilters.get(i);
            FilterBuilder termFilterBuilder = FilterBuilders.termFilter(fieldFilter.getField(), fieldFilter.getValue());
            andFilterBuilder.add(termFilterBuilder);
        }
    }

    private void buildRangeFilters(AndFilterBuilder andFilterBuilder) {
        List<RangeFilter> rangeFilters = getRangeFilters();
        if (rangeFilters.isEmpty()) {
            return;
        }
        for (int i = 0; i < rangeFilters.size(); i++) {
            RangeFilter fieldFilter = rangeFilters.get(i);
            FilterBuilder rangeFilterBuilder = FilterBuilders.rangeFilter(fieldFilter.getField()).from(fieldFilter.getFrom()).to(fieldFilter.getTo());
            andFilterBuilder.add(rangeFilterBuilder);
        }

    }

    public Integer getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Integer getFrom() {
        if (page == null || size == null) {
            return null;
        }
        return getSize() * (getPage() - 1);
    }

    public boolean isHighLightFields() {
        return highLightFields;
    }

    public ONSQueryBuilder setHighLightFields(boolean highLightFields) {
        this.highLightFields = highLightFields;
        return this;
    }

    public ONSQueryBuilder addSort(String fieldName, SortOrder sortOrder) {
        FieldSortBuilder sortBuilder = new FieldSortBuilder(fieldName).ignoreUnmapped(true);
        if (sortOrder != null) {
            sortBuilder.order(org.elasticsearch.search.sort.SortOrder.valueOf(sortOrder.name()));
        }
        this.sorts.add(sortBuilder);
        return this;
    }

    List<SortBuilder> getSorts() {
        return sorts;
    }
}
