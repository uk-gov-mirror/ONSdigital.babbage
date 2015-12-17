package com.github.onsdigital.babbage.search.model;

/**
 * Created by bren on 08/09/15.
 * <p/>
 * Content types.
 */
public enum ContentType {
    home_page,
    taxonomy_landing_page,
    product_page,
    bulletin,
    article,
    article_download,
    timeseries,
    data_slice,
    compendium_landing_page,
    compendium_chapter,
    compendium_data,
    static_landing_page,
    static_article,
    static_methodology,
    static_methodology_download,
    static_page, //Pure markdown
    static_qmi,
    static_foi,
    static_adhoc,
    dataset,
    dataset_landing_page,
    timeseries_dataset,
    release,
    reference_tables,
    chart,
    table;


    /**
     * Checks to see if type is one of the given content type
     *
     * @param type
     * @return
     */
    public static boolean isTypeIn(String type, ContentType... types) {
        try {
            ContentType contentType = ContentType.valueOf((String) type);
            for (ContentType currentType : types) {
                if (currentType.name().equals(type)) {
                    return true;
                }
            }
        } catch (IllegalArgumentException e) {
        }
        return false;
    }

    /**
     * Returns given content types' name as a string array
     *
     * @param types
     * @return
     */
    public static String[] getNamesOf(ContentType... types) {
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            ContentType type = types[i];
            names[i] = type.name();
        }
        return names;
    }
}
