package com.github.onsdigital.request.handler;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentClientException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.request.response.BabbageResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.github.onsdigital.babbage.util.RequestUtil.getCollectionId;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Serves rendered html output
 */
public class PageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "/";

    public static final String CONTENT_TYPE = "text/html";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws IOException, ContentNotFoundException, ContentClientException {
        ContentClient contentClient = new ContentClient(getCollectionId(request));
        ContentStream contentStream = contentClient.getContentStream(uri);
        String content = contentStream.getAsString();
        String navigation = contentClient.getNavigationData().getAsString();
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
