package com.github.onsdigital.babbage.search.builder;

/**
 * Created by bren on 07/09/15.
 *
 * Filter for field value
 *
 */
public class FieldFilter {
    private String field;
    private String value;

    public FieldFilter(String field, String value) {
        this.field = field;
        this.value = value;
    }


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
