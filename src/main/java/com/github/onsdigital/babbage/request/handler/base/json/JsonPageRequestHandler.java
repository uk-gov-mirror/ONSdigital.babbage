package com.github.onsdigital.babbage.request.handler.base.json;

import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by bren on 24/11/15.
 *
 * Request handlers that renders a page using json data. These handlers also serves json behind the page
 *
 */
public interface JsonPageRequestHandler {

    String JSON_ENDPOINT = "data";

    /**
     * Handles requests and returns html as response
     *
     * @param requestedUri
     * @param request
     */
    BabbageResponse getPage(String requestedUri, HttpServletRequest request) throws IOException;

    /**
     * Handles request and returns json as response
     *
     * @param requestedUri
     * @param request
     */
    BabbageResponse getData(String requestedUri, HttpServletRequest request) throws IOException;




}
