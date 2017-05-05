package com.github.onsdigital.babbage.search.model.sort;

import com.github.onsdigital.babbage.search.input.SortOrder;
import com.github.onsdigital.babbage.search.model.field.Field;


/**
 * Created by bren on 16/09/15.
 */
public class SortField {
    private Field field;
    private SortOrder order;

    public SortField(Field field, SortOrder order) {
        this.field = field;
        this.order = order;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }
}
