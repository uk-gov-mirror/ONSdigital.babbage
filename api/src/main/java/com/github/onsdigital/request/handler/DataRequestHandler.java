package com.github.onsdigital.request.handler;

import com.github.onsdigital.data.DataService;
import com.github.onsdigital.request.handler.base.RequestHandler;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bren on 28/05/15.
 */
public class DataRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "data";

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");
        try (InputStream input = DataService.getInstance().getDataStream(uri)) {
            IOUtils.copy(input, response.getOutputStream());
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
