package com.github.onsdigital.zebedee;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.error.ResourceNotFoundException;
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
 */
public class ZebedeeClient {

    final String authenticationHeader = "X-Florence-Token";

    CloseableHttpClient client;
    CloseableHttpResponse response;

    public void openConnection() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = HttpClients.createDefault();
                }
            }
        }
    }

    public InputStream startDataStream(ZebedeeRequest request) throws IOException {

        if (client == null) {
            openConnection();
        }

        String uriPath = request.getUri() + "/data.json";

        InputStream data = null;
        String url = Configuration.getZebedeeUrl() + "/content/" + request.getCollectionName();
        System.out.println("Calling zebedee: " + url + " for path " + uriPath + " with token: " + request.getAccessToken());

        HttpGet httpGet = new HttpGet(Configuration.getZebedeeUrl() + "/content/" + request.getCollectionName() + "?uri=" + uriPath);
        httpGet.addHeader(authenticationHeader, request.getAccessToken());

        response = client.execute(httpGet);

        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null && responseEntity.getContent() != null) {
            data = responseEntity.getContent();
        }

        System.out.println("Response: " + response.getStatusLine());


        return data;
    }

    public void closeConnection() {
        IOUtils.closeQuietly(response);
        IOUtils.closeQuietly(client);
    }
}
