package com.github.onsdigital.babbage.api.error;

import com.github.davidcarboni.restolino.api.RequestHandler;
import com.github.davidcarboni.restolino.framework.ServerError;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.error.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Handles exceptions and returns appropriate response to the client.
 */
public class ErrorHandler implements ServerError {

    private static void logError(Throwable e) {
        System.err.println(e.getMessage() + ", cause: " + (e.getCause() != null ? e.getCause().getMessage() : ""));
        e.printStackTrace();
    }

    @Override
    public Object handle(HttpServletRequest req, HttpServletResponse response, RequestHandler requestHandler, Throwable t) throws IOException {
        handle(req, response, t);
        return null;
    }

    public static void handle(HttpServletRequest req, HttpServletResponse response, Throwable t) throws IOException {
        logError(t);
        response.setContentType(MediaType.TEXT_HTML);

        if (ContentReadException.class.isAssignableFrom(t.getClass())) {
            ContentReadException exception = (ContentReadException) t;
            response.setStatus(exception.getStatusCode());
            try {
                renderErrorPage(exception.getStatusCode(), response);//renderTemplate template with status code name e.g. 404
                return;
            } catch (FileNotFoundException e) {
                System.out.println("No template found for error code, rendering 500. Error code: " + exception.getStatusCode());
            } catch (Exception e) {
                logError(e);
                System.out.println("Failed rendering error template, rendering 500, Error code:" + exception.getStatusCode());
            }
        }
        //todo: get rid of this exception type, all content should be read from content server
        else if (t instanceof ResourceNotFoundException) {
            try {
                renderErrorPage(404, response);
                return;
            } catch (Exception e) {
            }
        }
        renderErrorPage(500, response);
    }


    private static void renderErrorPage(int statusCode, HttpServletResponse response) throws IOException {
        String errorHtml = TemplateService.getInstance().renderTemplate("error/" + String.valueOf(statusCode));
        IOUtils.copy(new StringReader(errorHtml), response.getOutputStream());
    }
}