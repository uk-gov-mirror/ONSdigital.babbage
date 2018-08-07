package com.github.onsdigital.babbage.search.external.requests.search;

import com.github.onsdigital.babbage.search.external.SearchType;

public class FeaturedResultQuery extends SearchQuery {

    public FeaturedResultQuery(String searchTerm, ListType listType) {
        super(searchTerm, listType, SearchType.FEATURED);
    }
}
