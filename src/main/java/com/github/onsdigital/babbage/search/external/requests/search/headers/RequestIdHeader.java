package com.github.onsdigital.babbage.search.external.requests.search.headers;

import org.apache.http.message.BasicHeader;

public class RequestIdHeader extends BasicHeader {

    public static final String KEY = "X-Request-Id";

    private String requestId;

    public RequestIdHeader(String requestId) {
        super(KEY, requestId);
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }
}
