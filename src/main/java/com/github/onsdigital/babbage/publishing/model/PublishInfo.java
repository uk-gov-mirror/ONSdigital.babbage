package com.github.onsdigital.babbage.publishing.model;

import java.util.Date;

/**
 * Created by bren on 16/12/15.
 */
public class PublishInfo {

    private String uri;
    private String collectionId;
    private Date publishDate;

    public PublishInfo(String uri, String collectionId, Date publishDate) {
        this.uri = uri;
        this.collectionId = collectionId;
        this.publishDate = publishDate;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }
}
