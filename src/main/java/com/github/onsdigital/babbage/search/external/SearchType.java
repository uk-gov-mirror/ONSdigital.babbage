package com.github.onsdigital.babbage.search.external;

public enum SearchType {

    CONTENT("content", "result"),
    COUNTS("counts", "counts"),
    FEATURED("featured", "featuredResult"),
    DEPARTMENTS("departments", "departments");

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

    public static SearchType[] getBaseSearchTypes() {
        return new SearchType[]{SearchType.CONTENT, SearchType.COUNTS, SearchType.FEATURED};
    }
}
