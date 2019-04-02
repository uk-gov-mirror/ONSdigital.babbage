package com.github.onsdigital.babbage.api.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

public class RequestLogFilter implements Filter {

    @Override
    public boolean filter(HttpServletRequest req, HttpServletResponse res) {
        info().beginHTTP(req).log("request received");
        return true;
    }
}
