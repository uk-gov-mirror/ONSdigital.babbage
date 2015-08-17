package com.github.onsdigital.babbage.util.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private final URI HOST;

    public PooledHttpClient(String host) {
        HOST =  resolveHostUri(host);
        this.connectionManager = new PoolingHttpClientConnectionManager();
        this.httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

    private URI resolveHostUri(String host) {
        URI givenHost = URI.create(host);
        URIBuilder builder = new URIBuilder();
        if (StringUtils.startsWithIgnoreCase(host, "http")) {
            builder.setScheme(givenHost.getScheme());
            builder.setHost(givenHost.getHost());
            builder.setPort(givenHost.getPort());
            builder.setPath(givenHost.getPath());
            builder.setUserInfo(givenHost.getUserInfo());
        } else {
            builder.setScheme("http");
            builder.setHost(host);
        }
        try {
            return builder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param path            path, should not contain any query string, only path info
     * @param headers         key-value map to to be added to request as headers
     * @param queryParameters query parameters to be sent as get query string
     * @return
     * @throws IOException             All exceptions thrown are IOException implementations
     * @throws ClientProtocolException for protocol related exceptions, HttpResponseExceptions are a subclass of this exception type
     * @throws HttpResponseException   exception for http status code > 300, HttpResponseException is a subclass of IOException
     *                                 catch HttpResponseException for  status code
     */
    public CloseableHttpResponse sendGet(String path, Map<String, String> headers, List<NameValuePair> queryParameters) throws IOException {
        URI uri = buildGetUri(path, queryParameters);
        HttpGet request = new HttpGet(uri);
        addHeaders(headers, request);
        return validate(httpClient.execute(request));
    }


    /**
     * @param path            path, should not contain any query string, only path info
     * @param headers         key-value map to to be added to request as headers
     * @param postParameters query parameters to be sent as get query string
     * @return
     * @throws IOException             All exceptions thrown are IOException implementations
     * @throws ClientProtocolException for protocol related exceptions, HttpResponseExceptions are a subclass of this exception type
     * @throws HttpResponseException   exception for http status code > 300, HttpResponseException is a subclass of IOException
     *                                 catch HttpResponseException for  status code
     */
    public CloseableHttpResponse sendPost(String path, Map<String, String> headers, List<NameValuePair> postParameters) throws IOException {
        URI uri = buildPath(path);
        HttpPost request = new HttpPost(uri);
        addHeaders(headers, request);
        if (postParameters != null) {
            request.setEntity(new UrlEncodedFormEntity(postParameters));
        }
        return validate(httpClient.execute(request));
    }

    private void addHeaders(Map<String, String> headers, HttpRequestBase request) {
        if (headers != null) {
            Iterator<Map.Entry<String, String>> headerIterator = headers.entrySet().iterator();
            while (headerIterator.hasNext()) {
                Map.Entry<String, String> next = headerIterator.next();
                request.addHeader(next.getKey(), next.getValue());
            }

        }
    }

    public ClientConfiguration getConfiguration() {
        return new ClientConfiguration();
    }


    public void shutdown() throws IOException {
        httpClient.close();
    }

    private URI buildPath(String path) {
        URIBuilder uriBuilder = newUriBuilder(path);
        try {
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid uri! " + HOST  + path);
        }
    }

    private URIBuilder newUriBuilder(String path) {
        URIBuilder uriBuilder = new URIBuilder(HOST);
        uriBuilder.setPath((uriBuilder.getPath() + "/" + path).replaceAll("//+", "/"));
        return uriBuilder;
    }


    private URI buildGetUri(String path, List<NameValuePair> queryParameters) {
        try {
            URIBuilder uriBuilder = newUriBuilder(path);
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
            String errorMessage = getErrorMessage(entity);
            throw new HttpResponseException(
                    statusLine.getStatusCode(),
                    errorMessage == null ? statusLine.getReasonPhrase() : errorMessage);
        }
        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }

        return response;
    }

    private String getErrorMessage(HttpEntity entity) {
        try {
            String s = EntityUtils.toString(entity);
            return s;
        } catch (Exception e) {
            System.err.println("Failed reading content service:");
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Wrapping client connection manager setters to simplify configuration for single host
     */
    public class ClientConfiguration {

        private ClientConfiguration() {
        }

        public void setMaxConnection(int connectionNumber) {
            connectionManager.setMaxTotal(connectionNumber);
            connectionManager.setDefaultMaxPerRoute(connectionNumber);
        }

    }

    private class ShutdownHook extends Thread {
        @Override
        public void run() {
            System.out.println("Shutting down connection pool to host:" + HOST);
            try {
                if (httpClient != null) {
                    shutdown();
                }
                System.out.println("Successfully shut down connection pool");
            } catch (IOException e) {
                System.err.println("Falied shutting down connection pool");
                e.printStackTrace();
            }
        }
    }
}
