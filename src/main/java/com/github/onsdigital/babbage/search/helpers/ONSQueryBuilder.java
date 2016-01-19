package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.field.Field;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;

import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getResultsPerPage;

/**
 * Created by bren on 19/01/16.
 */
public class ONSQueryBuilder {

    private QueryBuilder builder;
    private AbstractAggregationBuilder[] aggregationBuilders;
    private int from;
    private int size = getResultsPerPage();//default size is in configuration
    private Integer page;
    private String[] types;
    private SortBy sortBy;
    private boolean highlight = true;
    private String[] fetchSource;

    public ONSQueryBuilder(QueryBuilder builder, String... types) {
        this.types = types;
        query(builder);
    }

    public QueryBuilder query() {
        return builder;
    }

    public ONSQueryBuilder query(QueryBuilder builder) {
        this.builder = builder;
        return this;
    }

    int from() {
        return from;
    }

    public int size() {
        return size;
    }

    public ONSQueryBuilder size(int size) {
        this.size = size;
        page(page);
        return this;
    }

    public ONSQueryBuilder page(Integer page) {
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

    public String[] types() {
        return types;
    }

    public ONSQueryBuilder types(String[] types) {
        this.types = types;
        return this;
    }

    public ONSQueryBuilder fetchFields(Field... fields) {
        fetchSource = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fetchSource[i] = fields[i].fieldName();
        }
        return this;
    }

    String[] fetchFields() {
        return fetchSource;
    }

    public SortBy sortBy() {
        return sortBy;
    }

    public ONSQueryBuilder sortBy(SortBy sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public boolean highlight() {
        return highlight;
    }

    public ONSQueryBuilder highlight(boolean highlight) {
        this.highlight = highlight;
        return this;
    }

    AbstractAggregationBuilder[] aggregate() {
        return aggregationBuilders;
    }

    public ONSQueryBuilder aggregate(AbstractAggregationBuilder... aggregations) {
        this.aggregationBuilders = aggregations;
        return this;
    }
}
