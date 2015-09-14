package com.github.onsdigital.babbage.search.helpers;

/**
 * Created by bren on 11/09/15.
 */
public enum SortBy {
    TITLE(Fields.title_raw, Fields.releaseDate, Fields._score),
    RELEVANCE(Fields._score, Fields.releaseDate),
    RELEASE_DATE(Fields.releaseDate, Fields._score);

    private Fields[] sortFields;

    SortBy(Fields... fields) {
        this.sortFields = fields;
    }

    public Fields[] getSortFields() {
        return sortFields;
    }
}
