//package com.github.onsdigital.babbage.request.handler.list;
//
//import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
//import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
//import com.github.onsdigital.babbage.search.model.ContentType;
//
///**
// * Render a list page for bulletins under the given URI.
// */
//public class DataListRequestHandler extends ListPageBaseRequestHandler implements RequestHandler {
//
//    private final static String REQUEST_TYPE = "datalist";
//    private final static ContentType[] ALLOWED_TYPES = {ContentType.dataset_landing_page, ContentType.reference_tables, ContentType.timeseries, ContentType.static_adhoc};
//
//    @Override
//    public String getRequestType() {
//        return REQUEST_TYPE;
//    }
//
//    @Override
//    public ContentType[] getAllowedTypes() { return ALLOWED_TYPES; } @Override public boolean isLocalisedUri() { return true; }
//
//    @Override
//    protected boolean isAggregateByType() {
//        return true;
//    }
//}
