package com.github.onsdigital.babbage.search.query.filter;

/**
 * Created by bren on 07/09/15.
 *
 * Filter for field value
 *
 */
public class FieldFilter {
    private String field;
    private Object value;

    public FieldFilter(String field, Object value) {
        this.field = field;
        this.value = value;
    }


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
