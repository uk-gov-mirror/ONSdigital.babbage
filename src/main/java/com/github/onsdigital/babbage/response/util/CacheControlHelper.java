package com.github.onsdigital.babbage.response.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bren on 09/12/15.
 */
public class CacheControlHelper {

    /**
     *  Resolves and sets response status based on request cache control headers and data to be sent to the user
     *
     * @param request
     * @return
     */
    public static void setCacheHeaders(HttpServletRequest request, HttpServletResponse response, String data) {
        resolve(request, response, DigestUtils.sha1Hex(data));
    }

    public static void setCacheHeaders(HttpServletRequest request, HttpServletResponse response, byte[] data) {
        resolve(request, response, DigestUtils.sha1Hex(data));
    }

    private static void resolve(HttpServletRequest request, HttpServletResponse response, String newHash) {
        String oldHash = getOldHash(request);
        System.out.println("Resolving cache headers, old has: " + oldHash + " new hash:" +  newHash);
        response.setHeader("Etag", newHash);
        if (StringUtils.equals(oldHash, newHash)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
    }

    private static String getOldHash(HttpServletRequest request) {
        String hash = request.getHeader("If-None-Match");
        return StringUtils.removeEnd(hash,"--gzip");//TODO: Restolino does not seem to be removing --gzip flag on etag when request comes in
    }
}
