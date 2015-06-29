package com.github.onsdigital.api.util;

import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.data.DataNotFoundException;
import com.github.onsdigital.error.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Handles exceptions and returns appropriate response to the client. It is possible to take actions for specific error types
 */
public class ApiErrorHandler {

    public static void handle(Exception e, HttpServletResponse response) throws IOException {

        logError(e);
        Map<String, String> errorResponse = new HashMap<String, String>();

        if (e instanceof ResourceNotFoundException) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            errorResponse.put("message", "Data you are looking for is not available");
            errorResponse.put("status", String.valueOf(HttpServletResponse.SC_NOT_FOUND));
        } else if ( e instanceof IllegalArgumentException ) {
            //TODO: Tidy up exception management
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errorResponse.put("message", e.getMessage() );
            errorResponse.put("status", String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            errorResponse.put("message", "Internal Server Error Occurred!");
            errorResponse.put("status", String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }
        Serialiser.serialise(response, errorResponse);
    }

    private static void logError(Exception e) {
        e.printStackTrace();
    }
}
