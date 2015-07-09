package com.github.onsdigital.request.response;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BabbageBinaryResponse extends BabbageResponse {

    private InputStream input;

    public BabbageBinaryResponse(InputStream input, String mimeType) {
        super(mimeType);
        this.input = input;
    }

    public void applyData(HttpServletResponse response) throws IOException {
        IOUtils.copy(input, response.getOutputStream());
        IOUtils.closeQuietly(input);
    }
}
