package com.github.onsdigital.babbage.search.external.requests.search.requests;

import com.github.onsdigital.babbage.search.external.SearchType;

/**
 * Replaces the internal type counts aggregation query by executing a HTTP request against the dp-conceptual-search counts API
 */
public class TypeCountsQuery extends SearchQuery {

    public TypeCountsQuery(String searchTerm, ListType listType) {
        super(searchTerm, listType, SearchType.COUNTS);
    }
}
