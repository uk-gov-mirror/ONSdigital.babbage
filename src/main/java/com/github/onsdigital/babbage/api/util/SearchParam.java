package com.github.onsdigital.babbage.api.util;

import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.github.onsdigital.babbage.search.model.filter.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchParam {

    private String searchTerm;
    private List<ContentType> docTypes = new ArrayList<>();
    private Integer page;
    private Integer size;
    private SortBy sortBy;
    private List<QueryType> queryTypes = new ArrayList<>();
    private List<Filter> filters = new ArrayList<>();
    private String aggregationField;


    SearchParam() {
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public SearchParam setSearchTerm(final String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    public List<ContentType> getDocTypes() {
        return docTypes;
    }

    public SearchParam addDocType(ContentType docTypes) {
        this.docTypes.add(docTypes);
        return this;
    }


    public SearchParam addDocTypes(ContentType... docTypes) {
        this.docTypes.addAll(Arrays.asList(docTypes));
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public SearchParam setPage(final Integer page) {
        this.page = page;
        return this;
    }

    public Integer getSize() {
        return size;
    }

    public SearchParam setSize(final Integer size) {
        this.size = size;
        return this;
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public SearchParam setSortBy(final SortBy sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public List<QueryType> getQueryTypes() {
        return queryTypes;
    }

    public SearchParam addQueryType(final QueryType queryType) {
        this.queryTypes.add(queryType);
        return this;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public SearchParam addFilter(final Filter filter) {
        this.filters.add(filter);
        return this;
    }

    public String getAggregationField() {
        return aggregationField;
    }

    public SearchParam setAggregationField(final String aggregationField) {
        this.aggregationField = aggregationField;
        return this;
    }

    public SearchParam addQueryTypes(final List<QueryType> queryTypes) {
        this.queryTypes.addAll(queryTypes);
        return this;
    }
}
