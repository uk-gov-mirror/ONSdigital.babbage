package com.github.onsdigital.babbage.search.model;

/**
 * Created by bren on 08/09/15.
 *
 * Content types.
 */
public enum ContentType {
    home_page,
    taxonomy_landing_page,
    product_page,
    bulletin,
    article,
    timeseries,
    data_slice,
    compendium_landing_page,
    compendium_chapter,//Resolve parent
    compendium_data,
    static_landing_page,
    static_article,
    static_methodology,
    static_page, //Pure markdown
    static_qmi,
    static_foi,
    static_adhoc,
    dataset,
    timeseries_dataset,
    release,
    reference_tables,
    chart,
    table
}
