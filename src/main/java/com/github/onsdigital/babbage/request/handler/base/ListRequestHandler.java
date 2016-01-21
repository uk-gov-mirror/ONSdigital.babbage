package com.github.onsdigital.babbage.request.handler.base;

import com.github.onsdigital.babbage.response.base.BabbageResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by bren on 20/01/16.
 */
public interface ListRequestHandler extends RequestHandler {
    BabbageResponse getData(String uri, HttpServletRequest request) throws IOException;
}
