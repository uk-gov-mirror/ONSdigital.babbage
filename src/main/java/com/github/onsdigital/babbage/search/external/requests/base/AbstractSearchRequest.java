package com.github.onsdigital.babbage.search.external.requests.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.search.external.SearchClient;
import org.apache.http.entity.ContentType;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;

import java.util.concurrent.Callable;

public abstract class AbstractSearchRequest<T> implements Callable<T> {

    protected static final String HOST = Configuration.SEARCH_SERVICE.getExternalSearchAddress();

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

    @Override
    public T call() throws Exception {
        String response = this.getContentResponse().getContentAsString();
        return MAPPER.readValue(response, this.returnClass);
    }

}
