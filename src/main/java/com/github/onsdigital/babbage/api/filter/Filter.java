package com.github.onsdigital.babbage.api.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A filter that is run before the HTTP request is processed.
 */
public interface Filter {

    /**
     * Return true if the request should continue to be processed.
     * @param req
     * @param res
     * @return
     */
    boolean filter(HttpServletRequest req, HttpServletResponse res);
}
