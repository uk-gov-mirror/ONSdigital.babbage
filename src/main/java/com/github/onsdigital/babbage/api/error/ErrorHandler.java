package com.github.onsdigital.babbage.api.error;

import com.github.davidcarboni.restolino.api.RequestHandler;
import com.github.davidcarboni.restolino.framework.ServerError;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by bren on 28/05/15.
 * <p/>
 * Handles exceptions and returns appropriate response to the client.
 */
public class ErrorHandler implements ServerError {

    private static void logError(Throwable e) {
        System.err.println(e.getMessage() + ", cause: " + (e.getCause() != null ? e.getCause().getMessage() : ""));
        ExceptionUtils.printRootCauseStackTrace(e);
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
        } else if (t instanceof ResourceNotFoundException) {
            renderErrorPage(404, response);
        } else {
            renderErrorPage(500, response);
        }
    }


    private static void renderErrorPage(int statusCode, HttpServletResponse response) throws IOException {
        try {
            response.setStatus(statusCode);
            //Prevent error pages being cached by cdn s
            response.addHeader("cache-control", "public, max-age=0" );
            Map<String, Object> context = new LinkedHashMap<>();
            context.put("type", "error");
            context.put("code", statusCode);
            String errorHtml = TemplateService.getInstance().renderContent(context);
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
