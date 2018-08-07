package com.github.onsdigital.babbage.search.external.requests.search;

import com.github.onsdigital.babbage.search.external.requests.search.exceptions.UnknownListTypeException;

public enum ListType {

    ONS("Search", "ons"),
    ONS_DATA("SearchData", "onsdata"),
    ONS_PUBLICATIONS("SearchPublication", "onspublications");

    private String listType;
    private String endpoint;

    ListType(String listType, String endpoint) {
        this.listType = listType;
        this.endpoint = endpoint;
    }

    public String getListType() {
        return listType;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public static ListType forString(String listType) throws UnknownListTypeException {
        for (ListType listTypeEnum : ListType.values()) {
            if (listTypeEnum.getListType().equalsIgnoreCase(listType)) {
                return listTypeEnum;
            }
        }
        throw new UnknownListTypeException(String.format("ListType unknown: %s", listType));
    }
}
