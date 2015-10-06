package com.github.onsdigital.babbage.request.handler.list;

/**
 * Created by bren on 06/10/15.
 */
public class AllAdhocsRequestHandler extends AdhocsRequestHandler {

    private final static String REQEUST_TYPE = "alladhocs";

    @Override
    public String getRequestType() {
        return REQEUST_TYPE;
    }

    @Override
    public boolean isLocalisedUri() {
        return false;
    }
}
