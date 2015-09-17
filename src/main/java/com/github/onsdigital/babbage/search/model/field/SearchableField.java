package com.github.onsdigital.babbage.search.model.field;

/**
 * Created by bren on 08/09/15.
 *
 * Searchable field names with boost factors
 */
public enum SearchableField {
    title(100),
    edition(100),
    summary,
    metaDescription,
    keywords,
    type;

    private double boostFactor = 1;

    SearchableField(double boostFactors) {
        this.boostFactor = boostFactors;
    }

    SearchableField() {

    }

    public double getBoostFactor() {
        return boostFactor;
    }

}
