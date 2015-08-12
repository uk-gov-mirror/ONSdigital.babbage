package com.github.onsdigital.babbage.api.filter;

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
 * Handles exceptions and returns appropriate response to the client. It is possible to take actions for specific error types
 */
public class ErrorHandler implements ServerError {

    private static void logError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public Object handle(HttpServletRequest req, HttpServletResponse response, RequestHandler requestHandler, Throwable t) throws IOException {
        logError(t);
        response.setContentType(MediaType.TEXT_HTML);

        if (ContentReadException.class.isAssignableFrom(t.getClass())) {
            ContentReadException exception = (ContentReadException) t;
            response.setStatus(exception.getStatusCode());
            try {
                renderErrorPage(exception.getStatusCode(), response);//render template with status code name e.g. 404
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
            } catch (Exception e) {
            }
        } else {
            renderErrorPage(500, response);
        }

        return null;
    }

    private void renderErrorPage(int statusCode, HttpServletResponse response) throws IOException {
        String errorHtml = TemplateService.getInstance().render("error/" + String.valueOf(statusCode), null);
        IOUtils.copy(new StringReader(errorHtml), response.getOutputStream());
    }
}
