package com.github.onsdigital.request.response;

import org.apache.commons.lang3.CharEncoding;

import java.io.Reader;

/**
 * Created by bren on 08/06/15.
 */
public class BabbageResponse {

    private String data;
    private String mimeType = "application/json"; //Default mimetype
    private String charEncoding = CharEncoding.UTF_8;//Default encoding


    public BabbageResponse(String data) {
        this.data = data;
    }

    public BabbageResponse(String data, String mimeType) {
        this(data);
        this.mimeType = mimeType;
    }

    public BabbageResponse(String data, String mimeType, String charEncoding) {
        this(data, mimeType);
        this.charEncoding = charEncoding;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getCharEncoding() {
        return charEncoding;
    }

    public void setCharEncoding(String charEncoding) {
        this.charEncoding = charEncoding;
    }
}
