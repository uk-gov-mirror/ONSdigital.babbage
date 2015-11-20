package com.github.onsdigital.babbage.response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * Sends a permanent moved response with new uri
 *
 */
public class BabbageResourceMovedResponse extends BabbageResponse {

    private String redirectUri;

    public BabbageResourceMovedResponse(String redirectAddress) {
        this.redirectUri = redirectAddress;
    }

    @Override
    public void apply(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", redirectUri);
    }

    @Override
    protected void applyData(HttpServletResponse response) throws IOException {}
}
