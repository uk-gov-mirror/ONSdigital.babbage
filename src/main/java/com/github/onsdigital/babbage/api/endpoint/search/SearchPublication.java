package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.search.model.ContentType;

/**
 * Created by bren on 21/09/15.
 */
@Api
public class SearchPublication extends Search {

    private final static ContentType[] ALLOWED_TYPES = {ContentType.article, ContentType.bulletin, ContentType.compendium_landing_page};

    @Override
    protected ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public String getRequestType() {
        return this.getClass().getSimpleName().toLowerCase();
    }

}
