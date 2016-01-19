//package com.github.onsdigital.babbage.request.handler.list;
//
//import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
//import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
//import com.github.onsdigital.babbage.search.model.ContentType;
//
///**
// * Created by bren on 06/10/15.
// */
//public class AllAdhocsRequestHandler extends ListPageBaseRequestHandler implements RequestHandler {
//
//    private final static String REQUEST_TYPE = "alladhocs";
//
//    private final static ContentType[] ALLOWED_TYPES = {ContentType.static_adhoc};
//
//    @Override
//    protected ContentType[] getAllowedTypes() {
//        return ALLOWED_TYPES;
//    }
//
//    @Override
//    public String getRequestType() {
//        return REQUEST_TYPE;
//    }
//
//    @Override
//    public boolean isLocalisedUri() {
//        return false;
//    }
//}
