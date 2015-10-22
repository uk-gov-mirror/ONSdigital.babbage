package com.github.onsdigital.babbage.search.model.field;

/**
 * Created by bren on 08/09/15.
 * <p/>
 * Searchable field names with boost factors
 */
public enum SearchableField {
    title_edition(100), //combined title and edition field, analyzed as one single field
    summary,
    metaDescription,
    keywords,
    type,
    cdid,
    datasetId;

    private Long boostFactor;

    SearchableField(long boostFactors) {
        this.boostFactor = boostFactors;
    }

    SearchableField() {

    }

    public Long getBoostFactor() {
        return boostFactor;
    }

}
