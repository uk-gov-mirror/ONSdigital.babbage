package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.FilterableField;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by bren on 22/09/15.
 */
public class ReleaseCal extends ListPageBaseRequestHandler {
    private final static ContentType[] ALLOWED_TYPES = {ContentType.release};
    private final static String REQEUST_TYPE = "releasecalendar";

    @Override
    protected ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public boolean useLocalisedUri() {
        return false;
    }

    @Override
    public String getRequestType() {
        return REQEUST_TYPE;
    }

    @Override
    protected SearchResponseHelper doSearch(HttpServletRequest request, ONSQuery query) throws IOException {
        String view = request.getParameter("view");
        boolean showPublished;
        if ("upcoming".equals(view)) {
            showPublished = false;
        } else {
            showPublished = true;
        }
        query.addFilter(FilterableField.published, showPublished);
        return super.doSearch(request, query);
    }
}
