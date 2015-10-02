package com.github.onsdigital.babbage.search.model.sort;

import com.github.onsdigital.babbage.search.model.field.FilterableField;
import org.elasticsearch.search.sort.SortOrder;

/**
 * Created by bren on 16/09/15.
 */
public class SortField {
    private FilterableField field;
    private SortOrder order;

    public SortField(FilterableField field, SortOrder order) {
        this.field = field;
        this.order = order;
    }

    public FilterableField getField() {
        return field;
    }

    public void setField(FilterableField field) {
        this.field = field;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }
}
