package com.github.onsdigital.babbage.search.external.requests.mocks.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.search.external.requests.mocks.json.MockSearchJson;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MockHttpEntity implements HttpEntity {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private MockSearchJson content;

    public MockHttpEntity(MockSearchJson mockJson) {
        this.content = mockJson;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public long getContentLength() {
        return 0;
    }

    @Override
    public Header getContentType() {
        return null;
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        String contentString = MAPPER.writeValueAsString(this.content.getSearchResult());
        return new ByteArrayInputStream(contentString.getBytes());
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {

    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void consumeContent() throws IOException {

    }
}
