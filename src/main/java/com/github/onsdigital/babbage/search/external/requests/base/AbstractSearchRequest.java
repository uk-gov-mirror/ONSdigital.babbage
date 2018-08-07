package com.github.onsdigital.babbage.search.external.requests.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.search.external.SearchClient;
import org.apache.http.entity.ContentType;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;

public abstract class AbstractSearchRequest<T> {

    public static final String HOST = "localhost:5000";

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    private final Class<T> returnClass;

    public AbstractSearchRequest(Class<T> returnClass) {
        this.returnClass = returnClass;
    }

    public abstract String targetUri();

    protected Request get() throws Exception {
        return SearchClient.get(this.targetUri());
    }

    protected Request post() {
        return SearchClient.post(this.targetUri())
                .header(HttpHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
    }

    protected abstract ContentResponse getContentResponse() throws Exception;

    public T execute() throws Exception {
        String response = this.getContentResponse().getContentAsString();
        return MAPPER.readValue(response, this.returnClass);
    }

}
