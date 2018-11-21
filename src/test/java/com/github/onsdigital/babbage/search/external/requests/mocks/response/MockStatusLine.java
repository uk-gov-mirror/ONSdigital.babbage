package com.github.onsdigital.babbage.search.external.requests.mocks.response;

import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;

public class MockStatusLine implements StatusLine {

    private int code;

    public MockStatusLine(int code) {
        this.code = code;
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return null;
    }

    @Override
    public int getStatusCode() {
        return this.code;
    }

    @Override
    public String getReasonPhrase() {
        return null;
    }
}
