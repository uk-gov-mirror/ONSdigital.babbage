package com.github.onsdigital.babbage.search.input;

import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.search.model.sort.SortField;
import org.elasticsearch.search.sort.SortOrder;

/**
 * Created by bren on 11/09/15.
 * <p/>
 * Defines what fields are to be sorted for each sort options that is selected on the pages
 */
public enum SortBy {
    //First letter skips non-letter character in the beginning. that's why sorting by first letter and then title
    first_letter(
            get(Field.title_first_letter, SortOrder.ASC),
            get(Field.title_raw, SortOrder.ASC),
            get(Field.releaseDate, SortOrder.ASC)
    ),
    title(
            get(Field.title_raw, SortOrder.ASC),
            get(Field.releaseDate, SortOrder.DESC)
    ),
    relevance(
            get(Field._score, SortOrder.DESC),
            get(Field.releaseDate, SortOrder.DESC)
    ),
    release_date(
            get(Field.releaseDate, SortOrder.DESC),
            get(Field._score, SortOrder.DESC)
    ),
    release_date_asc(
            get(Field.releaseDate, SortOrder.ASC),
            get(Field._score, SortOrder.DESC)
    );

    private final SortField[] sortFields;

    SortBy(SortField... sortFields) {
        this.sortFields = sortFields;
    }

    public SortField[] getSortFields() {
        return sortFields;
    }

    private static SortField get(Field field, SortOrder order) {
        return new SortField(field, order);
    }


}
