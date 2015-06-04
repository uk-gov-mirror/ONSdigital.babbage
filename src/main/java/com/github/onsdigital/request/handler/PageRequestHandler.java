package com.github.onsdigital.request.handler;

import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.template.TemplateService;
import org.apache.commons.lang3.CharEncoding;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Serves rendered html output
 */
public class PageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "/";

    public static final String CONTENT_TYPE = "text/html";

    @Override
    public Object handle(String requestedUri, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DataRequestHandler dataRequestHandler = new DataRequestHandler();
        String data = dataRequestHandler.getDataAsString(requestedUri, request);
//        response.getWriter().write(data);
        String html = TemplateService.getInstance().renderPage(data);
        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(html);
        return null;
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
