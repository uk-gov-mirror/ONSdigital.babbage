package com.github.onsdigital.babbage.search.helpers;

/**
 * Created by bren on 08/09/15.
 *
 * Searchable field names
 */
public enum SearchFields {
    title,
    edition,
    summary,
    metaDescription,
    keywords;

    public static String[] getAllSearchFields() {
        SearchFields[] values = SearchFields.values();
        String[] fields = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            fields[i] = values[i].name();
        }
        return fields;
    }
}
