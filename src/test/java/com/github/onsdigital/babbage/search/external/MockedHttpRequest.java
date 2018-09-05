package com.github.onsdigital.babbage.search.external;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.api.ContentResponse;

import java.net.URI;

public class MockedHttpRequest extends HttpRequest {
    public MockedHttpRequest(URI uri) {
        super(new HttpClient(), null, uri);
    }

    @Override
    public ContentResponse send() {
        return new MockedContentResponse();
    }
}
