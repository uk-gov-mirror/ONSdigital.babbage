package com.github.onsdigital.babbage.api.filter;

import com.github.davidcarboni.restolino.api.RequestHandler;
import com.github.davidcarboni.restolino.framework.ServerError;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.babbage.api.ErrorResponse;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentClientException;
import com.github.onsdigital.content.page.error.Error404;
import com.github.onsdigital.content.page.error.Error500;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.error.ResourceNotFoundException;
import com.github.onsdigital.template.TemplateService;
import com.github.onsdigital.util.NavigationUtil;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bren on 28/05/15.
 * <p/>
 * Handles exceptions and returns appropriate response to the client. It is possible to take actions for specific error types
 */
public class ErrorHandler implements ServerError {

    private static String HTML_CONTENT_TYPE = "text/html";

    private static void logError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public Object handle(HttpServletRequest req, HttpServletResponse response, RequestHandler requestHandler, Throwable t) throws IOException {
        logError(t);

        // when data is requested for content and it fails babbage returns json message instead of page with message from content service.
        //When page is requested Babbage renders appropriate error page html
        if (ContentClientException.class.isAssignableFrom(t.getClass())) {
            ContentClientException exception = (ContentClientException) t;
            response.setStatus(exception.getStatusCode());
            response.setContentType(MediaType.APPLICATION_JSON);
            Serialiser.serialise(response, new ErrorResponse(exception.getStatusCode() + ": " + exception.getMessage()));
        }
        //todo:get rid of this exception types as they are thrown when reading from file system. We will take out that bit of the code
        else if (t instanceof ResourceNotFoundException || t instanceof ContentNotFoundException) {
            Error404 error404 = new Error404();
            error404.setNavigation(NavigationUtil.getNavigation());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType(HTML_CONTENT_TYPE);
            IOUtils.copy(new StringReader(TemplateService.getInstance().renderPage(error404)), response.getOutputStream());
//            errorResponse.put("message", "Resource you are looking for is not available");
//            errorResponse.put("status", String.valueOf(HttpServletResponse.SC_NOT_FOUND));
        } else if (t instanceof IllegalArgumentException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Serialiser.serialise(new ErrorResponse(t.getMessage()));
        } else {
            Error500 error500 = new Error500();
            error500.setNavigation(NavigationUtil.getNavigation());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(HTML_CONTENT_TYPE);
            IOUtils.copy(new StringReader(TemplateService.getInstance().renderPage(error500)), response.getOutputStream());
//            errorResponse.put("status", String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
//            errorResponse.put("message", "Internal Server Error Occurred!");
        }

        return null;

    }
}
