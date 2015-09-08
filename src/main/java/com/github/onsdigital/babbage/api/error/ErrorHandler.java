package com.github.onsdigital.babbage.api.error;

import com.github.davidcarboni.restolino.api.RequestHandler;
import com.github.davidcarboni.restolino.framework.ServerError;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.error.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.ElasticsearchException;

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
            renderErrorPage(exception.getStatusCode(), response);//renderTemplate template with status code name e.g. 404
            return;
        } else if (t instanceof ElasticsearchException) {
            renderErrorPage(500, response);
        }
        //todo: get rid of this exception type, all content should be read from content server
        else if (t instanceof ResourceNotFoundException) {
            renderErrorPage(404, response);
        } else {
            renderErrorPage(500, response);
        }
    }


    public static void renderErrorPage(int statusCode, HttpServletResponse response) throws IOException {
        try {
            response.setStatus(statusCode);
            String errorHtml = TemplateService.getInstance().renderTemplate("error/" + String.valueOf(statusCode));
            IOUtils.copy(new StringReader(errorHtml), response.getOutputStream());
        } catch (Exception e) {
            if (statusCode != 500) {
                System.err.println("Failed rendering template for error code : " + statusCode + " rendering 500 template...");
                renderErrorPage(500, response);
            } else {
                System.err.println("!!!Warning!!! Rendering 500 template failed!!!!");
            }
            logError(e);
        }
    }
}
