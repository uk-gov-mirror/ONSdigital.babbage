package com.github.onsdigital.babbage.search.external.requests.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.search.external.SearchClient;
import com.github.onsdigital.babbage.search.external.requests.search.exceptions.InvalidSearchResponse;
import com.github.onsdigital.babbage.search.external.requests.search.headers.JsonContentTypeHeader;
import org.apache.commons.io.Charsets;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class AbstractSearchRequest<T> implements Callable<T> {

    protected static final String HOST = Configuration.SEARCH_SERVICE.getExternalSearchAddress();

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    private SearchClient searchClient;

    private Class<T> returnClass;
    private TypeReference<T> typeReference;

    public AbstractSearchRequest(Class<T> returnClass) {
        this.returnClass = returnClass;
    }

    public AbstractSearchRequest(TypeReference<T> typeReference) {
        this.typeReference = typeReference;
    }

    /**
     * Abstract method for building/returning the target URI for HTTP requests
     * @return
     */
    public abstract URIBuilder targetUri();

    private SearchClient getSearchClient() {
        if (searchClient == null) {
            searchClient = SearchClient.getInstance();
        }
        return searchClient;
    }

    public HttpGet get() throws URISyntaxException {
        return new HttpGet(this.targetUri().build());
    }

    public HttpPost post(Map<String, Object> params) throws URISyntaxException, JsonProcessingException {
        HttpPost post = new HttpPost(this.targetUri().build());
        post.addHeader(new JsonContentTypeHeader());

        if (null != params) {
            String postParams = buildPostParams(params);
            StringEntity stringEntity = new StringEntity(postParams, Charsets.UTF_8);
            post.setEntity(stringEntity);
        }
        return post;
    }

    /**
     * Abstract method for executing requests
     * @return
     * @throws Exception
     */
    protected abstract HttpRequestBase getRequestBase() throws Exception;

    @Override
    public T call() throws Exception {
        try (CloseableHttpResponse response = this.getSearchClient().execute(this.getRequestBase())) {
            String jsonResponse = EntityUtils.toString(response.getEntity());
            int code = response.getStatusLine().getStatusCode();

            if (code != HttpStatus.SC_OK) {
                throw new InvalidSearchResponse(jsonResponse, code);
            }

            // Either typeReference or returnClass are guaranteed to not be null
            if (this.typeReference != null) {
                return MAPPER.readValue(jsonResponse, this.typeReference);
            }

            return MAPPER.readValue(jsonResponse, this.returnClass);
        }
    }

    private static String buildPostParams(Map<String, Object> params) throws JsonProcessingException {
        return MAPPER.writeValueAsString(params);
    }

}
