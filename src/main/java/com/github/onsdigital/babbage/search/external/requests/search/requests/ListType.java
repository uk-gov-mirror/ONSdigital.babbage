package com.github.onsdigital.babbage.search.external.requests.search.requests;

import com.github.onsdigital.babbage.search.external.requests.search.exceptions.UnknownListTypeException;
import com.github.onsdigital.babbage.search.input.TypeFilter;

import java.util.Set;

public enum ListType {

    ONS("Search", "ons", TypeFilter.getAllFilters()),
    ONS_DATA("SearchData", "onsdata", TypeFilter.getDataFilters()),
    ONS_PUBLICATIONS("SearchPublication", "onspublications", TypeFilter.getPublicationFilters());

    private String listType;
    private String endpoint;
    private Set<TypeFilter> typeFilters;

    ListType(String listType, String endpoint, Set<TypeFilter> typeFilters) {
        this.listType = listType;
        this.endpoint = endpoint;
        this.typeFilters = typeFilters;
    }

    public String getListType() {
        return listType;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Set<TypeFilter> getTypeFilters() {
        return typeFilters;
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
