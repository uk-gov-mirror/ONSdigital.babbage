package com.github.onsdigital.babbage.response;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BabbageBinaryResponse extends BabbageResponse {

    private byte[] data;

    public BabbageBinaryResponse(InputStream data, String mimeType) throws IOException {
        super(mimeType);
        this.data = IOUtils.toByteArray(data);
    }

    public void applyData(HttpServletResponse response) throws IOException {
        try(ByteArrayInputStream input = new ByteArrayInputStream(data);) {
            IOUtils.copy(input, response.getOutputStream());
        }
    }
}
