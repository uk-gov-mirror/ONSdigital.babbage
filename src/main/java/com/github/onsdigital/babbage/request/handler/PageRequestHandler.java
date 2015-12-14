package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.template.TemplateService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Serves rendered html output
 */
public class PageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "/";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws IOException, ContentReadException {

        ContentResponse contentResponse = ContentClient.getInstance().getContent(uri);
        try (InputStream dataStream = contentResponse.getDataStream()){
            String html = TemplateService.getInstance().renderContent(dataStream);
            return new BabbageContentBasedStringResponse(contentResponse,html, TEXT_HTML);
        }
    }


    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
