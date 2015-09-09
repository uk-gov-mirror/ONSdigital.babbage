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
 * Handle data requests. Proxies data requests to content service
 */
public class DataRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "data";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return new BabbageStringResponse(getData(requestedUri, request));
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    public String getData(String uri, HttpServletRequest request) throws ContentNotFoundException, IOException, ContentReadException {
        try (ContentStream contentStream = ContentClient.getInstance().getContentStream(uri, getQueryParameters(request))) {
            return contentStream.getAsString();
        }

    }

}
