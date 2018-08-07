package com.github.onsdigital.babbage.search.external.requests.search;

import com.github.onsdigital.babbage.search.external.SearchType;

public class TypeCountsQuery extends SearchQuery {

    public TypeCountsQuery(String searchTerm, ListType listType) {
        super(searchTerm, listType, SearchType.COUNTS);
    }
}
