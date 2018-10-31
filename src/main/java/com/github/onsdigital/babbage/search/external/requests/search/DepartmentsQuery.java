package com.github.onsdigital.babbage.search.external.requests.search;

import com.github.onsdigital.babbage.search.external.SearchType;

public class DepartmentsQuery extends SearchQuery {

    public DepartmentsQuery(String searchTerm) {
        super(searchTerm, ListType.ONS, SearchType.DEPARTMENTS);
    }
}
