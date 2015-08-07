package com.github.onsdigital.babbage.content.client;

import com.github.onsdigital.babbage.util.http.PooledHttpClient;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.configuration.Configuration.CONTENT_SERVICE.getContentServiceUrl;
import static com.github.onsdigital.configuration.Configuration.CONTENT_SERVICE.getMaxContentServiceConnection;

/**
 * Created by bren on 23/07/15.
 * <p/>
 * Content service reads content from external server over http.
 * <p/>
 * Using Apache http client with connection pooling.
 *
 */
//TODO: Get http client use cache headers returned from content service
public class ContentClient {

    private static PooledHttpClient client;
    private static ContentClient instance;

    //Singleton
    private ContentClient() {
        System.out.println("Initializing content service http client");
        client = new PooledHttpClient(getContentServiceUrl());
        client.getConfiguration().setMaxConnection(getMaxContentServiceConnection());
    }

    public static ContentClient getInstance() {

        if (instance == null) {
            synchronized (ContentClient.class) {
                if (instance == null) {
                    instance = new ContentClient();
                }
            }
        }
        return instance;
    }

    /**
     * Serves requested content data, stream should be closed after use or fully consumed, fully consuming the stream will close the stream automatically
     * Content  client forwards any requests headers and cookies to content service using saved ThreadContext
     *
     * Any request headers like authentication tokens or collection ids should be saved to ThreadContext before calling content service
     *
     * @param uri uri for requested content.
     *            e.g./economy/inflationandpriceindices for content data requests
     *            e.g./economy/inflationandpriceindices/somecontent.xls  ( no data.json after the uri )
     *
     * @param queryParameters query parameters that needs to be passed to content service (e.g. filter parameters)
     * @return Json for requested content
     * @throws ContentReadException If content read fails due to an HTTP error status
     *                              Wraps protocol specific exceptions to remove dependency to underlying details to handle possible changes
     * @throws IOException          If reading content fails due to an unexpected error ( e.g. connection problems )
     */
    public ContentStream getContentStream(String uri, Map<String,String[]> queryParameters) throws ContentReadException, IOException {
        System.out.println("getContentStream(): Reading content from content server, uri:" + uri);
        CloseableHttpResponse response = null;
        try {
            response = client.sendGet(uri, getParameters(queryParameters));
            return new ContentStream(response);
        } catch (HttpResponseException e) {
            IOUtils.closeQuietly(response);
            throw wrapException(e);
        }
    }

    /**
     * Serves requested content data, stream should be closed after use or fully consumed, fully consuming the stream will close the stream automatically
     * Content  client forwards any requests headers and cookies to content service using saved ThreadContext
     *
     * Any request headers like authentication tokens or collection ids should be saved to ThreadContext before calling content service
     *
     * @param uri uri for requested content.
     *            e.g./economy/inflationandpriceindices for content data requests
     *            e.g./economy/inflationandpriceindices/somecontent.xls  ( no data.json after the uri )
     *
     * @return Json for requested content
     * @throws ContentReadException If content read fails due to an HTTP error status
     *                              Wraps protocol specific exceptions to remove dependency to underlying details to handle possible changes
     * @throws IOException          If reading content fails due to an unexpected error ( e.g. connection problems )
     */
    public ContentStream getContentStream(String uri) throws ContentReadException, IOException {
        return getContentStream(uri, null);
    }

    private List<NameValuePair> getParameters(Map<String, String[]> parametes) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        if (parametes != null) {
            for (Iterator<Map.Entry<String, String[]>> iterator = parametes.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String[]> entry = iterator.next();
                String[] values = entry.getValue();
                if (values == null) {
                    BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), null);
                }
                for (int i = 0; i < values.length; i++) {
                    String s = entry.getValue()[i];

                }

                nameValuePairs.add();
            }
        }
        return nameValuePairs;
    }


    private ContentReadException wrapException(HttpResponseException e) throws ContentReadException {
        throw new ContentReadException(e.getStatusCode(), e.getMessage(), e);
    }

}
