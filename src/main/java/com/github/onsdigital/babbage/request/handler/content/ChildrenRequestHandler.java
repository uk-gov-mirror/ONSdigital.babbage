package com.github.onsdigital.babbage.request.handler.content;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;

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
        try (ContentStream stream = ContentClient.getInstance().getChildren(uri, getQueryParameters(request))) {
            return new BabbageStringResponse(stream.getAsString());
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
