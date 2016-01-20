package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.Field;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;

import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getResultsPerPage;

/**
 * Created by bren on 19/01/16.
 *
 * ONS Content query encapsulating common parameters for search and list pages
 *
 */
public class ONSQuery {

    private QueryBuilder builder;
    private AbstractAggregationBuilder[] aggregationBuilders;
    private int from;
    private int size = getResultsPerPage();//default size is in configuration
    private Integer page;
    private ContentType[] types;
    private SortBy sortBy;
    private boolean highlight = false;
    private Field[] fetchFields;

    public ONSQuery(QueryBuilder builder, ContentType... types) {
        this.types = types;
        query(builder);
    }

    public QueryBuilder query() {
        return builder;
    }

    public ONSQuery query(QueryBuilder builder) {
        this.builder = builder;
        return this;
    }

    int from() {
        return from;
    }

    public int size() {
        return size;
    }

    public ONSQuery size(int size) {
        this.size = size;
        page(page);
        return this;
    }

    public ONSQuery page(Integer page) {
        this.page = page;
        if (page == null) {
            return this;
        } else if (page <= 1) {
            from = 0;
        } else {
            from = (page - 1) * size;
        }
        return this;
    }

    public Integer page() {
        return this.page;
    }

    public ContentType[] types() {
        return types;
    }

    public ONSQuery types(ContentType... types) {
        this.types = types;
        return this;
    }

    public ONSQuery fetchFields(Field... fields) {
        this.fetchFields = fields;
        return this;
    }

    Field[] fetchFields() {
        return fetchFields;
    }

    public SortBy sortBy() {
        return sortBy;
    }

    public ONSQuery sortBy(SortBy sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public boolean highlight() {
        return highlight;
    }

    public ONSQuery highlight(boolean highlight) {
        this.highlight = highlight;
        return this;
    }

    AbstractAggregationBuilder[] aggregate() {
        return aggregationBuilders;
    }

    public ONSQuery aggregate(AbstractAggregationBuilder... aggregations) {
        this.aggregationBuilders = aggregations;
        return this;
    }
}
