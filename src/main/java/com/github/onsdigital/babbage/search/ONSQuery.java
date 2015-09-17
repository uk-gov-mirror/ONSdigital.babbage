package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import com.github.onsdigital.babbage.search.model.field.SearchableField;
import com.github.onsdigital.babbage.search.model.filter.RangeFilter;
import com.github.onsdigital.babbage.search.model.filter.ValueFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bren on 07/09/15.
 */
public class ONSQuery {
    static final String HIGHLIGHTER_PRE_TAG = "<strong>";
    static final String HIGHLIGHTER_POST_TAG = "</strong>";

    private ContentType[] types;
    private SearchableField[] fields;
    private String uriPrefix;
    private String query;
    private List<ValueFilter> filters = new ArrayList<>();
    private List<RangeFilter> rangeFilters = new ArrayList<>();
    private List<SortBy> sorts = new ArrayList<>();
    private boolean highLightFields;
    private Integer page;
    private Integer size;

    public ONSQuery(ContentType... types) {
        this.types = types;
    }

    public ContentType[] getTypes() {
        return types;
    }

    public ONSQuery setTypes(ContentType... types) {
        this.types = types;
        return this;
    }

    public SearchableField[] getFields() {
        return fields;
    }

    String[] getFieldNames() {
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].name();
        }
        return fieldNames;
    }

    String[] getBoostedFieldNames() {
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].name() + "^" + fields[i].getBoostFactor();
        }
        return fieldNames;
    }

    public ONSQuery setFields(SearchableField... fields) {
        this.fields = fields;
        return this;
    }

    public String getUriPrefix() {
        return uriPrefix;
    }

    public ONSQuery setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public ONSQuery setQuery(String query) {
        this.query = query;
        return this;
    }

    public List<ValueFilter> getFilters() {
        return filters;
    }

    public ONSQuery addFilter(FilterableField field, Object... values) {
        filters.add(new ValueFilter(ValueFilter.FilterType.TERM, field, values));
        return this;
    }

    public ONSQuery addFilter(ValueFilter.FilterType filterType, FilterableField field, Object... values) {
        filters.add(new ValueFilter(filterType, field, values));
        return this;
    }

    public List<RangeFilter> getRangeFilters() {
        return rangeFilters;
    }

    public ONSQuery addRangeFilter(FilterableField field, Object from, Object to) {
        rangeFilters.add(new RangeFilter(field, from, to));
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public ONSQuery setPage(int page) {
        this.page = page;
        return this;
    }

    public Integer getSize() {
        return size;
    }

    public ONSQuery setSize(int size) {
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

    public ONSQuery setHighLightFields(boolean highLightFields) {
        this.highLightFields = highLightFields;
        return this;
    }

    public ONSQuery addSort(SortBy sortBy) {
        this.sorts.add(sortBy);
        return this;
    }

    public List<SortBy> getSorts() {
        return sorts;
    }
}
