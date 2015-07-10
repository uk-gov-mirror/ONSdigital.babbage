package com.github.onsdigital.api.home;

import com.github.davidcarboni.restolino.framework.Home;
import com.github.onsdigital.api.util.ApiErrorHandler;
import com.github.onsdigital.request.RequestDelegator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HomePage implements Home {

    @Override
    public Object get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            RequestDelegator.get(request, response);
        } catch (Throwable e) {
            ApiErrorHandler.handle(e, response);
        }
        return null;
    }
}
