package com.github.onsdigital.babbage.request.handler.content;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.babbage.request.response.BabbageResponse;
import com.github.onsdigital.babbage.request.response.BabbageStringResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.github.onsdigital.babbage.util.RequestUtil.getQueryParameters;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Serves rendered html output
 */
public class ChildrenRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "children";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws IOException, ContentNotFoundException, ContentReadException {
        return new BabbageStringResponse(ContentClient.getInstance().getChildren(uri, getQueryParameters(request)).getAsString());
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
