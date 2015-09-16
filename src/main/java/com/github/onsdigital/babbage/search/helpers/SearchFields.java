package com.github.onsdigital.babbage.search.helpers;

/**
 * Created by bren on 08/09/15.
 *
 * Searchable field names
 */
public enum SearchFields {
    title(100),
    edition(50),
    summary,
    metaDescription,
    keywords,
    type;

    private double boostFactor = 1;

    SearchFields(double boostFactors) {
        this.boostFactor = boostFactors;
    }

    SearchFields() {

    }

    public double getBoostFactor() {
        return boostFactor;
    }

}
