package com.github.onsdigital.api.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.request.response.BabbageResponse;
import com.github.onsdigital.babbage.request.response.BabbageStringResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.content.util.URIUtil;
import com.github.onsdigital.error.ResourceNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;

import static com.github.onsdigital.babbage.util.RequestUtil.getQueryParameters;

@Api
public class Search {
    private final static String HTML_MIME = "text/html";
    private final static String DATA_REQUEST = "data";
    private final static String SEARCH_REQUEST = "search";

    @GET
    public Object get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, ContentNotFoundException, ContentReadException {

        BabbageResponse babbageResponse;
        String type = URIUtil.resolveRequestType(request.getRequestURI());

        String uri = "search?" + request.getQueryString();

        switch (type) {
            case DATA_REQUEST:
                try (ContentStream contentStream = ContentClient.getInstance().getContentStream(uri, getQueryParameters(request))) {
                    babbageResponse = new BabbageStringResponse(contentStream.getAsString());
                }
                break;
            case SEARCH_REQUEST:
                try (InputStream dataStream = ContentClient.getInstance().getContentStream(uri, getQueryParameters(request)).getDataStream()) {
                    String html = TemplateService.getInstance().renderContent(dataStream);
                    babbageResponse = new BabbageStringResponse(html, HTML_MIME);
                }
                break;
            default:
                throw new ResourceNotFoundException();
        }
        babbageResponse.apply(response);
        return null;
    }
}