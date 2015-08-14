package com.github.onsdigital.request.handler;

import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.page.ContentRenderer;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;
import com.github.onsdigital.util.LocaleUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

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

        // todo: move this code up into the request delegator and pass the locale into this method?
        Locale locale = LocaleUtil.getLocaleFromUri(request.getRequestURI());
        requestedUri = LocaleUtil.trimLanguage(requestedUri);

        boolean jsEnhanced = isJsEnhanced(request);
        ContentRenderer pageRenderingService = new ContentRenderer(zebedeeRequest, jsEnhanced, locale);

        String dataFileName = "data.json";
//        if (!Locale.ENGLISH.equals(locale)) {
//            dataFileName = "data_" + locale.getLanguage() + ".json";
//        }

        try {
            Path dataPath = Paths.get(StringUtils.removeEnd(requestedUri, "/")).resolve(dataFileName);
            return new BabbageStringResponse(pageRenderingService.renderPage(dataPath.toString()), CONTENT_TYPE);
        } catch (ContentNotFoundException e) {
            return new BabbageStringResponse(pageRenderingService.renderPage(requestedUri + ".json"), CONTENT_TYPE);
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    /**
     * Return true if the client sends a jsEnhanced header.
     * @param request
     * @return
     */
    public static boolean isJsEnhanced(HttpServletRequest request) {
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
}
