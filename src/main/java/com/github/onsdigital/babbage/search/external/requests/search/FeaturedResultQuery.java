package com.github.onsdigital.babbage.search.external.requests.search;

import com.github.onsdigital.babbage.search.external.SearchType;

/**
 * Replaces the internal featured results query by executing a HTTP request against the dp-conceptual-search featured API
 */
public class FeaturedResultQuery extends SearchQuery {

    public FeaturedResultQuery(String searchTerm, ListType listType) {
        super(searchTerm, listType, SearchType.FEATURED);
    }
}
