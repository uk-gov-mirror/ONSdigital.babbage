package com.github.onsdigital.babbage.search.model.field;

/**
 * Created by bren on 08/09/15.
 * <p/>
 * Fields used for sorting and filtering, not analyzed in elastic search index
 */
public enum FilterableField {
    uri,
    title_raw,
    releaseDate,
    cdid,
    published,
    cancelled,
    keywords_raw,//raw means not analyzed, fields analyzed when indexing by default has _raw versions if needs to be filterable
    latestRelease,
    _score
}
