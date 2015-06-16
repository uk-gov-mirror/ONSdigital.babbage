package com.github.onsdigital.request.response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;

public class BabbageStringResponse extends BabbageResponse {

    private String data;
    private String charEncoding = CharEncoding.UTF_8;//Default encoding

    public BabbageStringResponse(String data) {
        this.data = data;
    }

    public BabbageStringResponse(String data, String mimeType) {
        super(mimeType);
        this.data = data;
    }

    public BabbageStringResponse(String data, String mimeType, String charEncoding) {
        this(data, mimeType);
        this.charEncoding = charEncoding;
    }

    public void apply(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(getCharEncoding());
        response.setContentType(getMimeType());
        IOUtils.copy(new StringReader(getData()), response.getOutputStream());
    }

    public String getData() {
        return data;
    }

    public String getCharEncoding() {
        return charEncoding;
    }
}
