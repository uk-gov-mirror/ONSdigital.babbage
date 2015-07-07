package com.github.onsdigital.request.response;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class BabbageBinaryResponse extends BabbageResponse {

    private InputStream input;

    public BabbageBinaryResponse(InputStream input, String mimeType) {
        super(mimeType);
        this.input = input;
    }

    public void apply(HttpServletResponse response) throws IOException {
        response.setContentType(getMimeType());
        IOUtils.copy(input, response.getOutputStream());
        IOUtils.closeQuietly(input);
    }
}
