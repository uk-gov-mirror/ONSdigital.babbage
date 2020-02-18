package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.google.gson.Gson;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Serves rendered html output
 */
public class PageRequestHandler extends BaseRequestHandler {

    private static final String REQUEST_TYPE = "/";
    private static final String PDF = "pdf";
    private static final String PDF_STYLE = "pdf_style";
    private static final String ENABLE_LOOP11 = "EnableLoop11";
    private static final String ENABLE_COOKIES_CONTROL = "EnableCookiesControl";
    private static final String COOKIES_PREFERENCES_SET = "CookiesPreferencesSet";
    private static final String COOKIES_POLICY = "CookiesPolicy";
    private static final String COOKIES_POLICY_NAME = "cookies_policy";


    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws IOException, ContentReadException {
        return getPage(uri, request);
    }

    public static BabbageResponse getPage(String uri, HttpServletRequest request) throws ContentReadException, IOException {
        ContentResponse contentResponse = ContentClient.getInstance().getContent(uri);
        try (InputStream dataStream = contentResponse.getDataStream()) {
            LinkedHashMap<String, Object> additionalData = new LinkedHashMap<>();
            if (RequestUtil.getQueryParameters(request).containsKey(PDF)) {
                additionalData.put(PDF_STYLE, true);
            }
            additionalData.put(ENABLE_LOOP11, appConfig().handlebars().isEnableLoop11());
            additionalData.put(ENABLE_COOKIES_CONTROL, appConfig().handlebars().isEnableCookiesControl());
            additionalData.put(COOKIES_PREFERENCES_SET, isCookiesPreferenceSet(request));

            Cookie cookiesPolicyCookie = getCookiesPolicy(request);
            if (cookiesPolicyCookie != null) {
                additionalData.put(COOKIES_POLICY, parseCookiesPolicy(cookiesPolicyCookie.getValue()));
            }
            String html = TemplateService.getInstance().renderContent(dataStream, additionalData);
            return new BabbageContentBasedStringResponse(contentResponse, html, TEXT_HTML);
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    static boolean isCookiesPreferenceSet(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        return getCookiesPolicy(request) != null;
    }

    static Cookie getCookiesPolicy(HttpServletRequest request) {
        if (request == null) {
            throw new NullPointerException("Request cannot be null");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        Cookie cookie = null;

        for (int i = 0; i < cookies.length; i++) {
            if (COOKIES_POLICY_NAME.equals(cookies[i].getName())) {
                cookie = cookies[i];
                break;
            }
        }

        return cookie;
    }

    static CookiesPolicy parseCookiesPolicy(String cookiePolicyValues) {
        if (cookiePolicyValues == null) {
            throw new NullPointerException("Cookies Policy values cannot be null");
        }
        Gson gson = new Gson();
        return gson.fromJson(cookiePolicyValues, CookiesPolicy.class);
    }
}
