package com.github.onsdigital.babbage.content.client;

import com.github.onsdigital.babbage.util.ThreadContext;
import com.github.onsdigital.babbage.util.http.PooledHttpClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.*;

import static com.github.onsdigital.configuration.Configuration.CONTENT_SERVICE.*;

/**
 * Created by bren on 23/07/15.
 * <p/>
 * Content service reads content from external server over http.
 * <p/>
 * Using Apache http client with connection pooling.
 */
//TODO: Get http client use cache headers returned from content service
public class ContentClient {

    private static PooledHttpClient client;
    private static String collectionId;

    //Singleton
    public ContentClient() {
        init();
    }

    public ContentClient(String collectionId) {
        init();
        this.collectionId = collectionId;
    }

    private void init() {
        if (client == null) {
            synchronized (ContentClient.class) {
                if (client == null) {
                    System.out.println("Initializing content service http client");
                    client = new PooledHttpClient(getContentServiceUrl());
                    client.getConfiguration().setMaxConnection(getMaxContentServiceConnection());
                }
            }
        }
    }

    /**
     * Serves requested content data, stream should be closed after use or fully consumed, fully consuming the stream will close the stream automatically
     * Content  client forwards any requests headers and cookies to content service using saved ThreadContext
     * <p/>
     * Any request headers like authentication tokens or collection ids should be saved to ThreadContext before calling content client
     *
     * @param uri             uri for requested content.
     *                        e.g./economy/inflationandpriceindices for content data requests
     *                        e.g./economy/inflationandpriceindices/somecontent.xls  ( no data.json after the uri )
     * @param queryParameters query parameters that needs to be passed to content service (e.g. filter parameters)
     * @return Json for requested content
     * @throws ContentClientException If content read fails due content service error status is set to whatever error is sent back from content service,
     *                                all other IO Exceptions are rethrown with HTTP status 500
     */
    public ContentStream getContentStream(String uri, Map<String, String[]> queryParameters) throws ContentClientException {
        System.out.println("getContentStream(): Reading content from content server, uri:" + uri);
        CloseableHttpResponse response = null;
        try {
            response = client.sendGet(getUri(), getHeaders(), getParameters(uri, queryParameters));
            return new ContentStream(response);
        } catch (HttpResponseException e) {
            throw wrapException(e);
        } catch (IOException e) {
            throw wrapException(e);
        } finally {
            IOUtils.closeQuietly(response);
        }
    }

    /**
     * Serves requested content data, stream should be closed after use or fully consumed, fully consuming the stream will close the stream automatically
     * Content  client forwards any requests headers and cookies to content service using saved ThreadContext
     * <p/>
     * Any request headers like authentication tokens or collection ids should be saved to ThreadContext before calling content service
     *
     * @param uri uri for requested content.
     *            e.g./economy/inflationandpriceindices for content data requests
     *            e.g./economy/inflationandpriceindices/somecontent.xls  ( no data.json after the uri )
     * @return Json for requested content
     * @throws ContentClientException If content read fails due content service error status is set to whatever error is sent back from content service,
     *                                all other IO Exceptions are rethrown with HTTP status 500
     */
    public ContentStream getContentStream(String uri) throws ContentClientException {
        return getContentStream(uri, null);
    }

    private List<NameValuePair> getParameters(String uri, Map<String, String[]> parametes) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();

        //uris are requested as get parameter from content service
        nameValuePairs.add(new BasicNameValuePair("uri", uri));
        if (parametes != null) {
            for (Iterator<Map.Entry<String, String[]>> iterator = parametes.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String[]> entry = iterator.next();
                String[] values = entry.getValue();
                if (ArrayUtils.isEmpty(values)) {
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), null));
                    continue;
                }
                for (int i = 0; i < values.length; i++) {
                    String s = entry.getValue()[i];
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), values[i]));
                }
            }
        }
        return nameValuePairs;
    }


    private ContentClientException wrapException(HttpResponseException e) {
        return new ContentClientException(e.getStatusCode(), e.getMessage(), e);
    }

    private ContentClientException wrapException(IOException e) {
        return new ContentClientException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Internal Server Error",e);
    }

    //Reads any parameters saved to thread context and sends it to content services as request headers
    private Map<String, String> getHeaders() {
        Map<String, String> cookies = new HashMap<>();
        Iterator<Map.Entry<String, Object>> iterate = ThreadContext.iterate();
        while (iterate.hasNext()) {
            Map.Entry<String, Object> next = iterate.next();
            Object value = next.getValue();
            cookies.put(next.getKey(), value == null ? null : value.toString());
        }
        return cookies;
    }

    private String getUri() {
        if (collectionId == null) {
            return getContentServiceDataEndpoint();
        } else {
            return getContentServiceDataEndpoint() + "/" + collectionId;
        }
    }
}
