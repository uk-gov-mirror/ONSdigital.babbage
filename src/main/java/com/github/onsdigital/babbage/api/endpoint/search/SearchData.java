package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.search.model.ContentType;

/**
 * Created by bren on 21/09/15.
 */
@Api
public class SearchData extends Search {

    private final static ContentType[] ALLOWED_TYPES = {ContentType.dataset_landing_page, ContentType.reference_tables, ContentType.timeseries, ContentType.static_adhoc};

    @Override
    protected ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    protected ContentType[] getAggregationTypes() {
        return super.getAllowedTypes();// count all documents types allowed in search.
    }

    @Override
    public String getRequestType() {
        return this.getClass().getSimpleName().toLowerCase();
    }

}
