package com.github.onsdigital.request.response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bren on 08/06/15.
 */
public abstract class BabbageResponse {

    private String mimeType = "application/json"; //Default mimetype

    public BabbageResponse(String mimeType) {
        this.mimeType = mimeType;
    }

    public BabbageResponse() { }

    public abstract void apply(HttpServletResponse response) throws IOException;

    public String getMimeType() {
        return mimeType;
    }
}
