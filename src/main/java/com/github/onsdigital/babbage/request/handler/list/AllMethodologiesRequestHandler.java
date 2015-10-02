package com.github.onsdigital.babbage.request.handler.list;

/**
 * Created by bren on 21/09/15.
 */
public class AllMethodologiesRequestHandler extends TopicSpecificMethodologyRequestHandler {

    private final static String REQEUST_TYPE = "allmethodologies";

    @Override
    public String getRequestType() {
        return REQEUST_TYPE;
    }

    @Override
    public boolean isLocalisedUri() {
        return false;
    }
}
