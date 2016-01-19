package com.github.onsdigital.babbage.search.input;

import com.github.onsdigital.babbage.search.model.ContentType;

/**
 * Created by bren on 16/09/15.
 * <p/>
 * Maps filters on search and list pages to actual content types to query. Most of the filters maps to multiple content types
 */
public enum TypeFilter {
    BULLETIN(ContentType.bulletin),
    ARTICLE(ContentType.article, ContentType.article_download),
    COMPENDIA(ContentType.compendium_landing_page),
    TIME_SERIES(ContentType.timeseries),
    DATASETS(ContentType.dataset_landing_page, ContentType.reference_tables),
    USER_REQUESTED_DATA(ContentType.static_adhoc),
    QMI(ContentType.static_qmi),
    METHODOLOGY(ContentType.static_qmi, ContentType.static_methodology, ContentType.static_methodology_download),
    METHODOLOGY_ARTICLE(ContentType.static_methodology, ContentType.static_methodology_download),
    CORPORATE_INFORMATION(ContentType.static_foi, ContentType.static_page, ContentType.static_landing_page, ContentType.static_article);

    private ContentType[] types;

    TypeFilter(ContentType... types) {
        this.types = types;
    }

    public ContentType[] getTypes() {
        return types;
    }
}
