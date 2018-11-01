package com.github.onsdigital.babbage.api.error;

import com.github.davidcarboni.restolino.api.RequestHandler;
import com.github.davidcarboni.restolino.framework.ServerError;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.error.LegacyPDFException;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.template.TemplateService;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.logging.LogBuilder.logEvent;

/**
 * Created by bren on 28/05/15.
 * <p/>
 * Handles exceptions and returns appropriate response to the client.
 */
public class ErrorHandler implements ServerError {

    @Override
    public Object handle(HttpServletRequest req, HttpServletResponse response, RequestHandler requestHandler, Throwable t) throws IOException {
        handle(req, response, t);
        return null;
    }

    public static void handle(HttpServletRequest req, HttpServletResponse response, Throwable t) throws IOException {
        response.setContentType(MediaType.TEXT_HTML);
        if (ContentReadException.class.isAssignableFrom(t.getClass())) {
            ContentReadException exception = (ContentReadException) t;
            renderErrorPage(exception.getStatusCode(), response);//renderTemplate template with status code name e.g. 404
            logEvent(t).error("errorHandler");
            return;
        } else if (t instanceof ResourceNotFoundException) {
            logEvent(t).error("ResourceNotFoundException error");
            renderErrorPage(404, response);
            return;
        } else if (t instanceof LegacyPDFException) {
            logEvent(t).error("LegacyPDFException error");
            renderErrorPage(501, response);
        } else {
            logEvent(t).error("Unknown error");
            renderErrorPage(500, response);
        }
    }


    public static void renderErrorPage(int statusCode, HttpServletResponse response) throws IOException {
        try {
            response.setStatus(statusCode);
            //Prevent error pages being cached by cdn s
            response.addHeader("cache-control", "public, max-age=0");
            Map<String, Object> context = new LinkedHashMap<>();
            context.put("type", "error");
            context.put("code", statusCode);
            String errorHtml = TemplateService.getInstance().renderContent(context);
            IOUtils.copy(new StringReader(errorHtml), response.getOutputStream());
        } catch (Exception e) {
            if (statusCode != 500) {
                logEvent(e).responseStatus(statusCode)
                        .error("error rendering template for status code, render 500 template");
                renderErrorPage(500, response);
            } else {
                logEvent(e).responseStatus(statusCode)
                        .error("error rendering 500 template");
            }
        }
    }
}
