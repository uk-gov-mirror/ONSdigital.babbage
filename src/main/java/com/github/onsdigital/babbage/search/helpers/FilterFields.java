package com.github.onsdigital.babbage.search.helpers;

/**
 * Created by bren on 08/09/15.
 *
 * Fields used for sorting and filtering, not analyzed in elastic search index
 */
public enum FilterFields {
    uri,
    title_raw,
    releaseDate,
    cdid
}
