package com.github.onsdigital.babbage.response;

import org.apache.commons.lang3.CharEncoding;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by bren on 08/06/15.
 *
 * A successful response for http request
 *
 */
public abstract class BabbageResponse {

    private String mimeType = APPLICATION_JSON; //Default mimetype
    private String charEncoding = CharEncoding.UTF_8;//Default encoding
    private Map<String, String> header;

    public BabbageResponse(String mimeType) {
        this.mimeType = mimeType;
    }

    public BabbageResponse() { }

    public void apply(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(getCharEncoding());
        response.setContentType(getMimeType());
        if (getHeader() != null) {
            Set<Map.Entry<String, String>> entries = getHeader().entrySet();
            for (Iterator<Map.Entry<String, String>> iterator = entries.iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> next = iterator.next();
                response.setHeader(next.getKey(), next.getValue());
            }
        }
        applyData(response);
    }

    protected abstract void applyData(HttpServletResponse response) throws IOException;

    public String getMimeType() {
        return mimeType;
    }

    public BabbageResponse setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public void addHeader(String key, String value) {
        if (header == null) {
            header = new HashMap<>();
        }
        header.put(key, value);
    }

    protected Map<String, String> getHeader() {
        return header;
    }

    public BabbageResponse setCharEncoding(String charEncoding) {
        this.charEncoding = charEncoding;
        return this;
    }

    public String getCharEncoding() {
        return charEncoding;
    }

}
