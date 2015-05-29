package com.github.onsdigital.api.util;

import com.github.onsdigital.data.DataNotFoundException;
import com.github.onsdigital.error.ResourceNotFoundException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Handles exceptions and returns appropriate response to the client. It is possible to take actions for specific error types
 */
public class ApiErrorHandler {

    public static Map<String, String> handle(Exception e, HttpServletResponse response) {

        logError(e);
        Map<String, String> errorResponse = new HashMap<String, String>();

        if (e instanceof ResourceNotFoundException) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            errorResponse.put("message", "Data you are looking for is not available");
            errorResponse.put("status", String.valueOf(HttpServletResponse.SC_NOT_FOUND));
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            errorResponse.put("message", "Internal Server Error Occurred!");
            errorResponse.put("status", String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }

        return errorResponse;
    }

    private static void logError(Exception e) {
        e.printStackTrace();
    }
}
