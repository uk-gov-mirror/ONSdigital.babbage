package com.github.onsdigital.request.handler;

import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.page.ContentRenderer;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Serves rendered html output
 */
public class PageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "/";

    public static final String CONTENT_TYPE = "text/html";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws IOException, ContentNotFoundException {
        return get(requestedUri, request, null);
    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws IOException, ContentNotFoundException {

        boolean jsEnhanced = isJsEnhanced(request);
        ContentRenderer pageRenderingService = new ContentRenderer(zebedeeRequest, jsEnhanced);

        try {
            Path dataPath = Paths.get(StringUtils.removeEnd(requestedUri, "/")).resolve("data.json");
            return new BabbageStringResponse(pageRenderingService.renderPage(dataPath.toString()), CONTENT_TYPE);
        } catch (ContentNotFoundException e) {
            return new BabbageStringResponse(pageRenderingService.renderPage(requestedUri + ".json"), CONTENT_TYPE);
        }
    }

    private boolean isJsEnhanced(HttpServletRequest request) {
        boolean jsEnhanced = false;
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return jsEnhanced;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("jsEnhanced")) {
                System.out.println("Found jsEnhanced cookie: " + cookie.getValue());
                jsEnhanced = Boolean.parseBoolean(cookie.getValue());
            }
        }
        return jsEnhanced;
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
