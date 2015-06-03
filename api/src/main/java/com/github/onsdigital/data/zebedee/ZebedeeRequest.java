package com.github.onsdigital.data.zebedee;

/**
 * Created by bren on 01/06/15.
 */
public class ZebedeeRequest {

    private String uri;
    private String collectionName;
    private String accessToken;

    public ZebedeeRequest(String uri, String collectionName, String authenticationToken) {
        this.uri = uri;
        this.collectionName = collectionName;
        this.accessToken = authenticationToken;
    }


    public String getUri() {
        return uri;
    }

    public String getAccessToken() {
        return accessToken;

    }

    public String getCollectionName() {
        return collectionName;

    }
}
