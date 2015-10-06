package com.github.onsdigital.babbage.search.model.field;

/**
 * Created by bren on 08/09/15.
 * <p/>
 * Searchable field names with boost factors
 */
public enum SearchableField {
    title(100),
    edition(10),
    summary,
    metaDescription,
    keywords,
    type;

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
