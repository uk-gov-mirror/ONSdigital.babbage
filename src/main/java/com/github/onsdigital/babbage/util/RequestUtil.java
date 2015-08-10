package com.github.onsdigital.babbage.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by bren on 10/08/15.
 */
public class RequestUtil {

    private static final String ACCESS_TOKEN_COOKIENAME = "access_token";
    private static final String TOKEN_HEADER = "X-Florence-Token";

    /**
     * Saves Authentication token and collection id to thread context if available when a request is made to babbage,
     * this ensures all data requests from content service can be authorized both  when data is requested for any purpose on Babbage ( rendering page, sending data back etc. )
     */
    public static void saveAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }


        String accessToken = getCookieValue(request, ACCESS_TOKEN_COOKIENAME);
        if (StringUtils.isNotEmpty(accessToken)) {
            //todo:delete this log for securiy reasons
            System.out.println("Found collection cookie: " + accessToken);
            ThreadContext.addData(TOKEN_HEADER, accessToken);
        }
    }

    public static void clearAllSaved() {
        ThreadContext.clear();
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * Extracts GET parameters from query string
     * <p/>
     * This method matches parameters to query string, if parameters is in query string it is return in the list of parameters.
     * <p/>
     * Note that a post parameters with the same name might also be included. There should not be parameters with same names in both get and post parameters if not wanted to be extracted
     */
    public static Map<String, String[]> getQueryStringParameters(HttpServletRequest request) {

        String queryString = request.getQueryString();
        if (StringUtils.isEmpty(queryString)) {
            return null;
        }
        Map<String, String[]> getParamets = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();

        Iterator<Map.Entry<String, String[]>> iterator = parameterMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String[]> next = iterator.next();
            if (queryString.contains(next.getKey() + "=")) {
                getParamets.put(next.getKey(), next.getValue());
            }
        }
        return getParamets;
    }

}
