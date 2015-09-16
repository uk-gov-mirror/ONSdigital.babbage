package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.search.helpers.Fields;
import com.github.onsdigital.babbage.search.helpers.SearchFields;
import com.github.onsdigital.babbage.search.query.SortOrder;
import com.github.onsdigital.babbage.search.query.filter.Filter;
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
    static final String HIGHLIGHTER_PRE_TAG = "<strong>";
    static final String HIGHLIGHTER_POST_TAG = "</strong>";

    private String[] types;
    private SearchFields[] fields;
    private String uriPrefix;
    private String query;
    private List<Filter> filters = new ArrayList<>();
    private List<RangeFilter> rangeFilters = new ArrayList<>();
    private boolean highLightFields;
    private List<SortBuilder> sorts = new ArrayList<>();
    private Integer page;
    private Integer size;

    public ONSQueryBuilder(String... types) {
        this.types = types;
    }

    public String[] getTypes() {
        return types;
    }

    public ONSQueryBuilder setTypes(String... types) {
        this.types = types;
        return this;
    }

    public SearchFields[] getFields() {
        return fields;
    }

    public String[] getFieldNames() {
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].name();
        }
        return fieldNames;
    }

    public String[] getBoostedFieldNames() {
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].name() + "^" + fields[i].getBoostFactor();
        }
        return fieldNames;
    }

    public ONSQueryBuilder setFields(SearchFields... fields) {
        this.fields = fields;
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

    public List<Filter> getFilters() {
        return filters;
    }

    public ONSQueryBuilder addFilter(String field, Object... values) {
        filters.add(new Filter(Filter.FilterType.TERM, field, values));
        return this;
    }

    public ONSQueryBuilder addFilter(Filter.FilterType filterType, String field, Object... values) {
        filters.add(new Filter(filterType, field, values));
        return this;
    }

    public List<RangeFilter> getRangeFilters() {
        return rangeFilters;
    }

    public ONSQueryBuilder addRangeFilter(String field, Object from, Object to) {
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
        return new MultiMatchQueryBuilder(getQuery(), getBoostedFieldNames());
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
        List<Filter> filters = getFilters();
        if (filters.isEmpty()) {
            return;
        }
        for (int i = 0; i < filters.size(); i++) {
            Filter filter = filters.get(i);
            FilterBuilder termFilterBuilder = FilterBuilders.termFilter(filter.getField(), filter.getValues());
            andFilterBuilder.add(termFilterBuilder);
        }
    }

    private void buildRangeFilters(AndFilterBuilder andFilterBuilder) {
        List<RangeFilter> rangeFilters = getRangeFilters();
        if (rangeFilters.isEmpty()) {
            return;
        }
        for (int i = 0; i < rangeFilters.size(); i++) {
            RangeFilter rangeFilter = rangeFilters.get(i);
            if (rangeFilter.getFrom() == null && rangeFilter.getTo() == null) {
                continue;
            }
            RangeFilterBuilder rangeFilterBuilder = FilterBuilders.rangeFilter(rangeFilter.getField());
            if (rangeFilter.getFrom() != null) {
                rangeFilterBuilder.from(rangeFilter.getFrom());
            }
            if (rangeFilter.getTo() != null) {
                rangeFilterBuilder.to(rangeFilter.getTo());
            }
            andFilterBuilder.add(rangeFilterBuilder);
        }

    }

    public Integer getSize() {
        return size;
    }

    public ONSQueryBuilder setSize(int size) {
        this.size = size;
        return this;
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

    public ONSQueryBuilder addSort(Fields field, SortOrder sortOrder) {
        FieldSortBuilder sortBuilder = new FieldSortBuilder(field.name()).ignoreUnmapped(true);
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
