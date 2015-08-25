package com.github.onsdigital.babbage.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bren on 10/08/15.
 */
public class RequestUtil {

    /**
     * Saves Authentication token and collection id to thread context if available when a request is made to babbage,
     * this ensures all data requests from content service can be authorized both  when data is requested for any purpose on Babbage ( rendering page, sending data back etc. )
     */
    public static void saveRequestContext(HttpServletRequest request) {
        ThreadContext.addData("cookies", getAllCookies(request));
        ThreadContext.addData("parameters", request.getParameterMap());

    }

    public static void clearContext() {
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

    public static Map<String, String> getAllCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Map<String, String> cookiesMap = new HashMap<>();
        if (cookies == null) {
            return cookiesMap;
        }
        for (Cookie cookie : cookies) {
            cookiesMap.put(cookie.getName(), cookie.getValue());
        }
        return cookiesMap;
    }

    /**
     * Extracts GET parameters from query string
     * <p>
     * This method matches parameters to query string, if parameters is in query string it is return in the list of parameters.
     * <p>
     * Note that a post parameters with the same name might also be included. There should not be parameters with same names in both get and post parameters if not wanted to be extracted
     */
    public static Map<String, String[]> getQueryParameters(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String, String[]> queryParameters = new HashMap<>();

        if (request == null || request.getQueryString() == null ||
                request.getQueryString().length() == 0) {
            return queryParameters;
        }

        String queryString = URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8.name());

        if (StringUtils.isEmpty(queryString)) {
            return queryParameters;
        }

        String[] parameters = queryString.split("&");

        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=");
            String[] values = queryParameters.get(keyValuePair[0]);
            values = ArrayUtils.add(values, keyValuePair.length == 1 ? "" : keyValuePair[1]); //length is one if no value is available.
            queryParameters.put(keyValuePair[0], values);
        }
        return queryParameters;
    }

}
