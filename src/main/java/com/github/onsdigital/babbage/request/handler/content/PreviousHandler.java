package com.github.onsdigital.babbage.request.handler.content;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.template.TemplateService;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.LinkedHashMap;

/**
 * Created by bren on 10/11/15.
 */
public class PreviousHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "previous";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {

        ContentResponse contentResponse = ContentClient.getInstance().getContent(uri);
        try (InputStream stream = contentResponse.getDataStream()) {
            LinkedHashMap<String, Object> additionalData = new LinkedHashMap<>();
            //overwriting page type for template rendering
            additionalData.put("previous", true);//Setting previous flag for template context to make use of
            String html = TemplateService.getInstance().renderContent(stream, additionalData);
            return new BabbageContentBasedStringResponse(contentResponse, html, "text/html");
        }

    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
