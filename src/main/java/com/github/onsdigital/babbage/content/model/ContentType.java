package com.github.onsdigital.babbage.content.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bren on 08/09/15.
 *
 * Content types.
 *
 * todo: cleaner design
 * Content types marked as searchable will be allowed to be listed on search page by default.
 */
public enum ContentType {
    home_page(false),
    taxonomy_landing_page(false),
    product_page(false),
    bulletin(true),
    article(true),
    timeseries(true),
    data_slice(true),
    compendium_landing_page(true),
    compendium_chapter(false),//Resolve parent
    compendium_data(false),
    static_landing_page(false),
    static_article(true),
    static_methodology(true),
    static_page(false), //Pure markdown
    static_qmi(true),
    static_foi(false),
    static_adhoc(false),
    dataset(true),
    timeseries_dataset(true),
    release(false),
    release_list(false),
    reference_tables(true),
    chart(false),
    table(false),
    unknown(false);

    private boolean searchable;
    private static String[] searchableTypes = resolveSearchableTypes();

    ContentType(boolean searchable) {
        this.searchable = searchable;
    }

    private static String[] resolveSearchableTypes() {
        ContentType[] values = ContentType.values();
        List<String> searchableFields = new ArrayList<>();
        for (ContentType value : values) {
            if (value.isSearchable()) {
                searchableFields.add(value.name());
            }
        }
        String[] fields = new String[searchableFields.size()];
        return  searchableFields.toArray(fields);
    }

    public static String[] getSearchableTypes() {
        return searchableTypes;
    }

    public boolean isSearchable() {
        return searchable;
    }
}
