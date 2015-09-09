package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.content.service.ContentNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Serves rendered html output
 */
public class PageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "/";

    public static final String CONTENT_TYPE = "text/html";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws IOException, ContentNotFoundException, ContentReadException {

        try (InputStream dataStream = ContentClient.getInstance().getContentStream(uri).getDataStream()) {
            String html = TemplateService.getInstance().renderContent(dataStream);
            return new BabbageStringResponse(html, CONTENT_TYPE);
        }
    }


    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
