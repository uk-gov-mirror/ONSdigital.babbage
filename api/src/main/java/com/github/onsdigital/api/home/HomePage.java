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
        //TODO:Dlete HomePage ( no longer needed, not found delegates urls to handlers)
        try {
            return RequestDelegator.handle(request, response);
        } catch (Exception e) {
            return ApiErrorHandler.handle(e, response);
        }
    }

}
