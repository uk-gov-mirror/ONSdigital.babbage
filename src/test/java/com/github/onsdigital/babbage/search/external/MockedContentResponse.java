package com.github.onsdigital.babbage.search.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.search.model.SearchResult;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpVersion;

import java.io.IOException;
import java.util.List;

public class MockedContentResponse implements ContentResponse {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String getMediaType() {
        return null;
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public byte[] getContent() {
        return new byte[0];
    }

    @Override
    public String getContentAsString() {
        try {
            SearchResult testResult = TestSearchResponseUtils.testSearchResult();
            return MAPPER.writeValueAsString(testResult);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Request getRequest() {
        return null;
    }

    @Override
    public <T extends ResponseListener> List<T> getListeners(Class<T> aClass) {
        return null;
    }

    @Override
    public HttpVersion getVersion() {
        return null;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getReason() {
        return null;
    }

    @Override
    public HttpFields getHeaders() {
        return null;
    }

    @Override
    public boolean abort(Throwable throwable) {
        return false;
    }
}
