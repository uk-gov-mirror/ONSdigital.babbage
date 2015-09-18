package com.github.onsdigital.babbage.search.model.filter;

import com.github.onsdigital.babbage.search.model.field.FilterableField;

/**
 * Created by bren on 07/09/15.
 *
 * Filter for field value
 *
 */
public class ValueFilter {
    private FilterableField field;
    private Object[] values;
    private FilterType filterType;

    public ValueFilter(FilterType filterType, FilterableField field, Object... values) {
        this.field = field;
        this.values = values;
        this.filterType = filterType;
    }

    public FilterableField getField() {
        return field;
    }

    public void setField(FilterableField field) {
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
        PREFIX, /**Filters with prefix*/
        TERM /**Filters with exact match*/
    }
}
