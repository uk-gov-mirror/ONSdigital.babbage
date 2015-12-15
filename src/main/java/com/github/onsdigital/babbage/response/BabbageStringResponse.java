package com.github.onsdigital.babbage.response;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;

public class BabbageStringResponse extends BabbageResponse {

    private String data;

    public BabbageStringResponse(String data) {
        this.data = data;
    }

    public BabbageStringResponse(String data, String mimeType) {
        super(mimeType);
        this.data = data;
    }

    public BabbageStringResponse(String data, String mimeType, String charEncoding) {
        this(data, mimeType);
        setCharEncoding(charEncoding);
    }

    protected void applyData(HttpServletResponse response) throws IOException {
        // https://acunetix.com/vulnerabilities/web/clickjacking--x-frame-options-header-missing
        response.addHeader("X-Frame-Options", "SAMEORIGIN"); // DENY | SAMEORIGIN
        IOUtils.copy(new StringReader(getData()), response.getOutputStream());
    }

    public String getData() {
        return data;
    }

}
