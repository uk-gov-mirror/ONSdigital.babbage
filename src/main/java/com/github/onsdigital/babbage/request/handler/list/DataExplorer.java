package com.github.onsdigital.babbage.request.handler.list;

/**
 * Created by bren on 21/09/15.
 */
public class DataExplorer extends DataListRequestHandler {

    private final static String REQEUST_TYPE = "dataexplorer";

    @Override
    public String getRequestType() {
        return REQEUST_TYPE;
    }

    @Override
    public boolean useLocalisedUri() {
        return false;
    }
}
