package com.github.onsdigital.babbage.request.handler.list;

/**
 * Created by bren on 21/09/15.
 */
public class DataExplorer extends DataListRequestHandler {

    private final static String REQUEST_TYPE = "dataexplorer";

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    @Override
    public boolean isLocalisedUri() {
        return false;
    }
}
