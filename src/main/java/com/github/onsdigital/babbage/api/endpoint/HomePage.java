package com.github.onsdigital.babbage.api.endpoint;

import com.github.davidcarboni.restolino.framework.Home;
import com.github.onsdigital.babbage.api.error.ErrorHandler;
import com.github.onsdigital.babbage.request.RequestDelegator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HomePage implements Home {

    @Override
    public Object get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            RequestDelegator.get(request, response);
        } catch (Throwable t) {
            new ErrorHandler().handle(request, response, null, t);
        }
        return null;
    }
}
