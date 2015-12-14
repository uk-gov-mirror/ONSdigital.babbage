package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.URIUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.LinkedHashMap;

/**
 * Handles requests at the endpoint /table.
 * Renders a chart and associated content in an isolated page.
 */
public class TableRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "table";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        ContentResponse jsonResponse = ContentClient.getInstance().getContent(requestedUri);
        ContentResponse html = ContentClient.getInstance().getResource(URIUtil.cleanUri(requestedUri) + ".html");
        LinkedHashMap<String, Object> htmlEntry = new LinkedHashMap<>();
        htmlEntry.put("html", html.getAsString());
        String jsonString = jsonResponse.getAsString();
        String result = TemplateService.getInstance().renderTemplate("table", jsonString, htmlEntry);
        return new BabbageContentBasedStringResponse(jsonResponse, result, MediaType.TEXT_HTML);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
