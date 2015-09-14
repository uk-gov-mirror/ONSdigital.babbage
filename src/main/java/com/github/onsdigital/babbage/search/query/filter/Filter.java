package com.github.onsdigital.babbage.search.query.filter;

/**
 * Created by bren on 07/09/15.
 *
 * Filter for field value
 *
 */
public class Filter {
    private String field;
    private Object[] values;
    private FilterType filterType;

    public Filter(FilterType filterType, String field, Object... values) {
        this.field = field;
        this.values = values;
        this.filterType = filterType;
    }


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public enum FilterType{
        PREFIX,
        TERM
    }
}
