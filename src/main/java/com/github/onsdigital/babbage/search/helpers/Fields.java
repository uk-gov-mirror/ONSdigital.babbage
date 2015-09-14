package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.search.query.SortOrder;

/**
 * Created by bren on 08/09/15.
 *
 * Fields used for sorting and filtering, not analyzed in elastic search index
 */
public enum Fields {
    uri(SortOrder.ASC),
    title_raw(SortOrder.ASC),
    releaseDate(SortOrder.DESC),
    cdid(SortOrder.ASC),
    keywords_raw(SortOrder.ASC),
    latestRelease(SortOrder.ASC),
    _score(SortOrder.DESC);

    private SortOrder sortOrder;

    Fields(SortOrder order) {
        this.sortOrder = order;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }
}
