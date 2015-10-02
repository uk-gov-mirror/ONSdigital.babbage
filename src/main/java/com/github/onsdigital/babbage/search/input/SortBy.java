package com.github.onsdigital.babbage.search.input;

import com.github.onsdigital.babbage.search.model.field.FilterableField;
import com.github.onsdigital.babbage.search.model.sort.SortField;
import org.elasticsearch.search.sort.SortOrder;

/**
 * Created by bren on 11/09/15.
 */
public enum SortBy {
    TITLE(
            get(FilterableField.title_raw, SortOrder.ASC),
            get(FilterableField.releaseDate, SortOrder.DESC)
    ),
    RELEVANCE(
            get(FilterableField._score, SortOrder.DESC),
            get(FilterableField.releaseDate, SortOrder.DESC)
    ),
    RELEASE_DATE(
            get(FilterableField.releaseDate, SortOrder.DESC),
            get(FilterableField._score, SortOrder.DESC)
    );

    private SortField[] sortFields;

    SortBy(SortField... sortFields) {
        this.sortFields = sortFields;
    }

    public SortField[] getSortFields() {
        return sortFields;
    }

    private static SortField get(FilterableField field, SortOrder order) {
        return new SortField(field, order);
    }
}
