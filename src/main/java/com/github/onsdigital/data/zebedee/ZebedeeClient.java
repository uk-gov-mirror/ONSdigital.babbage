package com.github.onsdigital.data.zebedee;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.data.DataNotFoundException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bren on 01/06/15.
 *
 * Zebedeeclient is not thread-safe
 */
public class ZebedeeClient {


    final String authenticationHeader = "X-Florence-Token";
    final String resolve = "resolve";

    CloseableHttpClient client;
    CloseableHttpResponse response;
    ZebedeeRequest zebedeeRequest;


    public ZebedeeClient(ZebedeeRequest zebedeeRequest) {
        this.zebedeeRequest = zebedeeRequest;
    }


    public void openConnection() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = HttpClients.createDefault();
                }
            }
        }
    }

    private InputStream startDataStream(String uri, boolean resolveReferences) throws IOException, ContentNotFoundException {

        if (client == null) {
            openConnection();
        }

        String collection = zebedeeRequest.getCollectionName();
        String authToken = zebedeeRequest.getAccessToken();

        String uriPath;
        if ("/".equals(uri)) {
            uriPath = uri + "data.json";
        } else {
            uriPath = uri;
        }

        InputStream data = null;
        String url = Configuration.getZebedeeUrl() + "/content/" + collection;
        System.out.println("Calling zebedee: " + url + " for path " + uriPath + " with token: " + authToken);
        url += "?uri=" + uriPath;
        if (resolveReferences) {
            url += "&resolve";
        }

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(authenticationHeader, authToken);


        response = client.execute(httpGet);

//        handleResponse(response.getStatusLine(), url);

        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null && responseEntity.getContent() != null) {
            data = responseEntity.getContent();
        }

        System.out.println("Response: " + response.getStatusLine());

        return data;
    }

    public void closeConnection() {
        IOUtils.closeQuietly(client);
    }

    private void throwInternalError(String message) {
        throw new RuntimeException("Failed reading from Zebedee");
    }

    public InputStream readData(String uri, boolean resolveReferences) throws ContentNotFoundException {

        InputStream dataStream = null;
        try {
            dataStream = startDataStream(uri, resolveReferences);
            if (dataStream != null) {
                return dataStream;
            } else {
                closeConnection();
                throw new DataNotFoundException(uri);
            }
        } catch (IOException e) {
            //TODO: Update client library for possible content read error exception typess
            closeConnection();
            throw new RuntimeException("Failed reading data from zebedee",e);
        }

    }

}
