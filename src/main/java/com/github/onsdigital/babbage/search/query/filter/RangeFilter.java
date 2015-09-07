package com.github.onsdigital.babbage.search.query.filter;

/**
 * Created by bren on 07/09/15.
 *
 * Range filter for a field
 *
 */
public class RangeFilter {

    private String field;
    private Object from;
    private Object to;

    public RangeFilter(String field, Object from , Object to) {
        this.field = field;
        this.from = from;
        this.to = to;
    }


    public String getField() {
        return field;
    }

    public void setField(String field) {
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
