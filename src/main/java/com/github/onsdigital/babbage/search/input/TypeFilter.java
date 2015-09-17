package com.github.onsdigital.babbage.search.input;

import com.github.onsdigital.babbage.search.model.ContentType;

/**
 * Created by bren on 16/09/15.
 */
public enum TypeFilter {
    BULLETIN(ContentType.bulletin),
    ARTICLE(ContentType.article),
    COMPENDIA(ContentType.compendium_landing_page),
    SINGLE_TIME_SERIES(ContentType.timeseries),
    LARGE_DATASET(ContentType.dataset, ContentType.timeseries_dataset, ContentType.reference_tables),
    QMI(ContentType.static_qmi),
    METHODOLOGY(ContentType.static_qmi, ContentType.static_methodology),
    METHODOLOGY_ARTICLE(ContentType.static_methodology);

    private ContentType[] types;

    TypeFilter(ContentType... types) {
        this.types = types;
    }

    public ContentType[] getTypes() {
        return types;
    }

}
