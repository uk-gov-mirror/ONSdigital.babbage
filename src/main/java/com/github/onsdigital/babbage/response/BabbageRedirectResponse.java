package com.github.onsdigital.babbage.response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * Sends a redirect to the client
 *
 */
public class BabbageRedirectResponse extends BabbageResponse {

    private String redirectUri;

    public BabbageRedirectResponse(String redirectAddress) {
        this.redirectUri = redirectAddress;
    }

    @Override
    public void apply(HttpServletResponse response) throws IOException {
        response.sendRedirect(redirectUri);
    }

    @Override
    protected void applyData(HttpServletResponse response) throws IOException {}
}
