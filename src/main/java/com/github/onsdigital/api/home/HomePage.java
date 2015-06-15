package com.github.onsdigital.api.home;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.davidcarboni.restolino.framework.Home;
import com.github.onsdigital.api.util.ApiErrorHandler;
import com.github.onsdigital.request.RequestDelegator;

public class HomePage implements Home {

    @Override
    public Object get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            RequestDelegator.get(request, response);
        } catch (Exception e) {
            ApiErrorHandler.handle(e, response);
        }
        return null;
    }

}
