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

    @Override
    public void apply(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(redirectUri);
    }
}
