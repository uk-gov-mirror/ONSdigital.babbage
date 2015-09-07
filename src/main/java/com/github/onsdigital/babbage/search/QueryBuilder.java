package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.search.builder.FieldFilter;
import com.github.onsdigital.babbage.search.builder.RangeFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bren on 07/09/15.
 */
public class QueryBuilder {

    private static final String PRE_TAG = "<strong>";
    private static final String POST_TAG = "</strong>";
    private static final int SIZE = Configuration.GENERAL.getResultsPerPage();

    String index;
    String[] types;
    String uriPrefix;
    String query;
    List<FieldFilter> fieldFilters = new ArrayList<>();
    List<RangeFilter> rangeFilters = new ArrayList<>();

    int page = 1;

    public QueryBuilder(String index, String... types) {
        this.index = index;
        this.types = types;
    }

    public String getIndex() {
        return index;
    }

    public QueryBuilder setIndex(String index) {
        this.index = index;
        return this;
    }


    public String[] getTypes() {
        return types;
    }

    public QueryBuilder setTypes(String... types) {
        this.types = types;
        return this;
    }

    public String getUriPrefix() {
        return uriPrefix;
    }

    public QueryBuilder setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public QueryBuilder setQuery(String query) {
        this.query = query;
        return this;
    }

    public List<FieldFilter> getFieldFilters() {
        return fieldFilters;
    }

    public QueryBuilder addFilter(String field, String value) {
        fieldFilters.add(new FieldFilter(field,value));
        return this;
    }

    public List<RangeFilter> getRangeFilters() {
        return rangeFilters;
    }

    public QueryBuilder addRangeFilter(String field, String from, String to) {
        rangeFilters.add(new RangeFilter(field, from, to));
        return this;
    }

    public int getPage() {
        return page;
    }

    public QueryBuilder setPage(int page) {
        this.page = page;
        return this;
    }

    private int calculateFrom() {
        return SIZE * (getPage() - 1);
    }

}
