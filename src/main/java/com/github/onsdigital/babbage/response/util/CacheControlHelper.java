package com.github.onsdigital.babbage.response.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.github.onsdigital.babbage.logging.LogEvent.logEvent;

/**
 */
public class CacheControlHelper {

    /**
     * Resolves and sets response status based on request cache control headers and data to be sent to the user
     *
     * @param request
     * @return
     */
    public static void setCacheHeaders(HttpServletRequest request, HttpServletResponse response, String hash, long maxAge) {
        resolveHash(request, response, hash);
        setMaxAage(response, maxAge);
    }

    public static void setCacheHeaders(HttpServletResponse response, long maxAge) {
        setMaxAage(response, maxAge);
    }

    private static void setMaxAage(HttpServletResponse response, long maxAge) {
        response.addHeader("cache-control", "public, max-age=" + maxAge);
    }

    public static String hashData(String data) {
        return DigestUtils.sha1Hex(data);
    }

    public static String hashData(byte[] data) {
        return DigestUtils.sha1Hex(data);
    }

    private static void resolveHash(HttpServletRequest request, HttpServletResponse response, String newHash) {
        if (StringUtils.isEmpty(newHash)) {
            return;
        }
        String oldHash = getOldHash(request);

        logEvent()
                .parameter("oldHash", oldHash)
                .parameter("newHash", newHash)
                .info("resolving cache headers");

        response.setHeader("Etag", newHash);
        if (StringUtils.equals(oldHash, newHash)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
    }

    private static String getOldHash(HttpServletRequest request) {
        String hash = request.getHeader("If-None-Match");
        return StringUtils.remove(hash, "--gzip");//TODO: Restolino does not seem to be removing --gzip flag on etag when request comes in
    }
}
