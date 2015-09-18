package com.github.onsdigital.babbage.search.model.filter;

import com.github.onsdigital.babbage.search.model.field.FilterableField;

/**
 * Created by bren on 07/09/15.
 *
 * Range filter for a field
 *
 */
public class RangeFilter {

    private FilterableField field;
    private Object from;
    private Object to;

    public RangeFilter(FilterableField field, Object from , Object to) {
        this.field = field;
        this.from = from;
        this.to = to;
    }


    public FilterableField getField() {
        return field;
    }

    public void setField(FilterableField field) {
        this.field = field;
    }

    public Object getFrom() {
        return from;
    }

    public void setFrom(Object from) {
        this.from = from;
    }

    public Object getTo() {
        return to;
    }

    public void setTo(Object to) {
        this.to = to;
    }
}
