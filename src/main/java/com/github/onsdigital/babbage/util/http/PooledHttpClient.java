package com.github.onsdigital.babbage.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bren on 22/07/15.
 * <p/>
 * http client to a single host with connection pool and  cache functionality.
 */
//TODO: SSL support for https?
//Add post,put,etc. functionality if needed
public class PooledHttpClient {

    private final PoolingHttpClientConnectionManager connectionManager;
    private final CloseableHttpClient httpClient;
    private final String HOST;
    private final ClientConfiguration configuration;

    public PooledHttpClient(String host) {
        this.HOST = host;
        this.connectionManager = new PoolingHttpClientConnectionManager();
        this.httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
        this.configuration = new ClientConfiguration();
    }


    /**
     * @param path path, should not contain any query string, only path info
     * @return response
     * @throws IOException             All exceptions thrown are IOException implementations
     * @throws ClientProtocolException for protocol related exceptions, HttpResponseExceptions are a subclass of this exception type
     * @throws HttpResponseException   exception for http status code > 300, HttpResponseException is a subclass of IOException
     *                                 catch HttpResponseException for  status code
     */
    public CloseableHttpResponse sendGet(String path) throws IOException {
        return sendGet(path, null, null);
    }


    /**
     * @param path path, should not contain any query string, only path info
     * @param queryParameters query parameters to be sent as get query string
     * @return response
     * @throws IOException             All exceptions thrown are IOException implementations
     * @throws ClientProtocolException for protocol related exceptions, HttpResponseExceptions are a subclass of this exception type
     * @throws HttpResponseException   exception for http status code > 300, HttpResponseException is a subclass of IOException
     *                                 catch HttpResponseException for  status code
     */
    public CloseableHttpResponse sendGet(String path, List<NameValuePair> queryParameters) throws IOException {
        return sendGet(path, null, queryParameters);
    }

    /**
     * @param path path, should not contain any query string, only path info
     * @param cookies       key-value map to to be added to request as cookie headers
     * @return response
     * @throws IOException             All exceptions thrown are IOException implementations
     * @throws ClientProtocolException for protocol related exceptions, HttpResponseExceptions are a subclass of this exception type
     * @throws HttpResponseException   exception for http status code > 300, HttpResponseException is a subclass of IOException
     *                                 catch HttpResponseException for  status code
     */
    public CloseableHttpResponse sendGet(String path, Map<String, String> cookies) throws IOException {
        return sendGet(path, cookies, null);
    }

    /**
     * @param path       path, should not contain any query string, only path info
     * @param headers       key-value map to to be added to request as cookie headers
     * @param queryParameters query parameters to be sent as get query string
     * @return
     * @throws IOException             All exceptions thrown are IOException implementations
     * @throws ClientProtocolException for protocol related exceptions, HttpResponseExceptions are a subclass of this exception type
     * @throws HttpResponseException   exception for http status code > 300, HttpResponseException is a subclass of IOException
     *                                 catch HttpResponseException for  status code
     */
    public CloseableHttpResponse sendGet(String path, Map<String,String> headers, List<NameValuePair> queryParameters) throws IOException {
        URI uri = buildUri(path, queryParameters);
        System.out.println("Sending get request to " + uri);
        HttpGet request = new HttpGet(uri);
        if (headers != null) {
            Iterator<Map.Entry<String, String>> headerIterator = headers.entrySet().iterator();
            while (headerIterator.hasNext()) {
                Map.Entry<String, String> next = headerIterator.next();
                request.addHeader(next.getKey(), next.getValue());
            }

        }
        return validate(httpClient.execute(request));
    }

    public ClientConfiguration getConfiguration() {
        return new ClientConfiguration();
    }



    private URI buildUri(String path, List<NameValuePair> queryParameters) {
        try {
            URIBuilder uriBuilder = new URIBuilder().setScheme("http").setHost(HOST).setPath(path);
            if (queryParameters != null) {
                uriBuilder.setParameters(queryParameters);
            }
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid uri! " + HOST + path);
        }
    }


    /**
     * Throws appropriate errors if response is not successful
     */
    private CloseableHttpResponse validate(CloseableHttpResponse response) throws ClientProtocolException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(
                    statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }

        return response;
    }


    /***
     * Wrapping client connection manager setters to simplify configuration for single host
     *
     */
    public class ClientConfiguration {

        private ClientConfiguration() {
        }

        public void setMaxConnection(int connectionNumber) {
            connectionManager.setMaxTotal(connectionNumber);
            connectionManager.setDefaultMaxPerRoute(connectionNumber);
        }

    }


}
