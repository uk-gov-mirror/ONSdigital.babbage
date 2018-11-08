package com.github.onsdigital.babbage.search.external.requests.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.search.external.SearchClient;
import com.github.onsdigital.babbage.search.external.requests.search.exceptions.SearchErrorResponse;
import com.github.onsdigital.babbage.search.external.requests.search.headers.JsonContentTypeHeader;
import com.github.onsdigital.babbage.search.external.requests.search.headers.RequestIdHeader;
import org.apache.commons.io.Charsets;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.babbage.logging.LogEvent.logEvent;

public abstract class AbstractSearchRequest<T> implements Callable<T> {

    protected static final String HOST = appConfig().externalSearch().address();

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    private SearchClient searchClient;

    private Class<T> returnClass;
    private TypeReference<T> typeReference;
    private final RequestIdHeader requestIdHeader = new RequestIdHeader(UUID.randomUUID().toString());

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

    private SearchClient getSearchClient() throws Exception {
        if (searchClient == null) {
            searchClient = SearchClient.getInstance();
        }
        return searchClient;
    }

    public HttpGet get() throws URISyntaxException {
        HttpGet get = new HttpGet(this.targetUri().build());
        get.addHeader(this.requestIdHeader);
        return get;
    }

    public HttpPost post(Map<String, Object> params) throws URISyntaxException, JsonProcessingException {
        HttpPost post = new HttpPost(this.targetUri().build());
        post.addHeader(this.requestIdHeader);
        post.addHeader(new JsonContentTypeHeader());

        if (null != params) {
            String postParams = buildPostParams(params);
            StringEntity stringEntity = new StringEntity(postParams, Charsets.UTF_8);
            post.setEntity(stringEntity);
        }

        return post;
    }

    public final String getRequestId() {
        return requestIdHeader.getRequestId();
    }

    /**
     * Abstract method for executing requests
     * @return
     * @throws Exception
     */
    public abstract HttpRequestBase getRequestBase() throws Exception;

    @Override
    public T call() throws Exception {
        try (CloseableHttpResponse response = this.getSearchClient().execute(this)) {
            String jsonResponse = EntityUtils.toString(response.getEntity());
            int code = response.getStatusLine().getStatusCode();

            if (code != HttpStatus.SC_OK) {
                logEvent()
                        .requestID(this.getRequestBase())
                        .responseStatus(code)
                        .error("External search service returned non 200 response");
                throw new SearchErrorResponse(jsonResponse, code, this.getRequestId());
            }

            // Either typeReference or returnClass are guaranteed to not be null
            if (this.typeReference != null) {
                return MAPPER.readValue(jsonResponse, this.typeReference);
            }

            return MAPPER.readValue(jsonResponse, this.returnClass);
        } catch (Exception e) {
            // Log failure with request context then re-throw
            logEvent().info(String.format("Error executing external search request [context=%s]", this.getRequestId()));
            throw e;
        }
    }

    private static String buildPostParams(Map<String, Object> params) throws JsonProcessingException {
        return MAPPER.writeValueAsString(params);
    }

}
