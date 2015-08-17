package com.github.onsdigital.request.handler;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.request.response.BabbageResponse;
import com.github.onsdigital.babbage.request.response.BabbageStringResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.URIUtil;
import com.github.onsdigital.babbage.util.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.LinkedHashMap;

/**
 * Render a list page for the given URI.
 */
public class PreviousReleasesRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "previousreleases";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        LinkedHashMap<String, Object> uri = new LinkedHashMap<>();
        uri.put("uri", requestedUri);
        String html =  TemplateService.getInstance().renderTemplate("content/t9-6", JsonUtil.toJson(uri));
        return  new BabbageStringResponse(html, MediaType.TEXT_HTML);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
