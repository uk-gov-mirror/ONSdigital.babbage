package com.github.onsdigital.babbage.response;

import com.github.onsdigital.babbage.response.base.BabbageResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sends a redirect to the client
 */
public class BabbageRedirectResponse extends BabbageResponse {

    private String redirectUri;

    public BabbageRedirectResponse(String redirectAddress) {
        this.redirectUri = redirectAddress;
    }

    public BabbageRedirectResponse(String redirectAddress, Long maxAge) {
        this(redirectAddress);
        setMaxAge(maxAge);
    }

    @Override
    public void apply(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCacheHeaders(request, response);
//        response.sendRedirect(redirectUri);  // old http redirect

        String url = buildHttpsRedirectUrl(request);
        response.sendRedirect(url);
    }

    private static String serverName(HttpServletRequest request) {
        return request.getServerName();
    }

    /**
     * Build a HTTPS redirect
     * @param request
     * @return
     */
    private static String buildHttpsRedirectUrl(HttpServletRequest request) {
        String url = "https://" + request.getServerName()
                + request.getContextPath() + request.getServletPath();

        return url;
    }
}
