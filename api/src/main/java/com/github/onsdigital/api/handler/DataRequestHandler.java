package com.github.onsdigital.api.handler;

import com.github.onsdigital.data.DataService;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bren on 28/05/15.
 */
public class DataRequestHandler {

    public void handleDataRequest(String uri, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");
        try (InputStream input = DataService.getInstance().getDataStream(uri)) {
            IOUtils.copy(input, response.getOutputStream());
        }
    }


}
