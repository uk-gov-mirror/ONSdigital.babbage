package com.github.onsdigital.api.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Several utilities for formatting and cleaning requested resource uri
 */
public class URIUtils {

    //Represents uris starting with endpoint names. e.g. /data/economy/...
    private final static Pattern endpointPrefixedUrlPattern = Pattern.compile("/+([^/]*)/+(.*)/*");

    //Represents uris ending in requested content type. e.g. /economy/data or economy/inflationpriceandindices/chart
    private final static Pattern requestTypeSuffixedUrlPattern = Pattern.compile("/*(.*)/+(.*)");


    /**
     * Extracts uri from endpoint prefixed uris
     * <p>
     * e.g. uri for /data/economy/inflationandpriceindices is /economy/inflationandpriceindices
     *
     * @return The URI path, lowercased, without the endpoint name or trailing
     * slash.
     */
    public static String extractUri(String uriString) {
        Matcher matcher = endpointPrefixedUrlPattern.matcher(uriString);
        String result = matcher.group(2);
        // Lowercase
        result.toLowerCase();
        return result;
    }


    /**
     * Extracts uri and request type from request type suffixed uris.
     * <p>
     * e.g.
     * for uri "/economy/inflationandpriceindices/data" request type is "data" and uri is "/economy/inflationandpriceindices"
     *
     * @return [request type][requested resource uri]
     */
    public static String[] resolveRequestUri(String uriString) {
        Matcher matcher = requestTypeSuffixedUrlPattern.matcher(uriString);
        String[] result = new String[2];
        result[0] = matcher.group(2);
        result[1] = matcher.group(1);
    }

}
