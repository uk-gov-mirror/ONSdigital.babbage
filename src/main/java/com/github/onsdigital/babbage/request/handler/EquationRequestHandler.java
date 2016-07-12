package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.equations.MathjaxRenderer;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.util.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Handles requests at the endpoint /equation.
 * Renders a equation and associated content in an isolated page.
 */
public class EquationRequestHandler extends BaseRequestHandler {

    private static final String REQUEST_TYPE = "equation";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {

        // get the json for the equation.
        ContentResponse jsonResponse = ContentClient.getInstance().getContent(requestedUri);
        Map<String, Object> objectMap = JsonUtil.toMap(jsonResponse.getDataStream());

        String equationInput = objectMap.get("content").toString();
        String equationOuput = MathjaxRenderer.render(equationInput);

        return new BabbageStringResponse(equationOuput, MediaType.TEXT_HTML);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
