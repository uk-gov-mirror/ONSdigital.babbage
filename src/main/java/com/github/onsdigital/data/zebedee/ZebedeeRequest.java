package com.github.onsdigital.data.zebedee;

/**
 * Created by bren on 01/06/15.
 */
public class ZebedeeRequest {

    private String collectionName;
    private String accessToken;

    public ZebedeeRequest( String collectionName, String authenticationToken) {
        this.collectionName = collectionName;
        this.accessToken = authenticationToken;
    }

    public String getAccessToken() {
        return accessToken;

    }

    public String getCollectionName() {
        return collectionName;

    }

}
