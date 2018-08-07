package com.github.onsdigital.babbage.search.external;

public enum SearchType {

    CONTENT("content", "result"),
    COUNTS("counts", "counts"),
    FEATURED("featured", "featuredResult");

    private String searchType;
    private String resultKey;

    SearchType(String searchType, String resultKey) {
        this.searchType = searchType;
        this.resultKey = resultKey;
    }

    public String getSearchType() {
        return searchType;
    }

    public String getResultKey() {
        return resultKey;
    }
}
