package com.github.onsdigital.babbage.publishing.model;

import java.util.Date;
import java.util.List;

/**
 * Created by bren on 16/12/15.
 */
public class PublishNotification {
    private String key;
    private String collectionId;
    private List<String> uriList;
    private Date publishDate;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getUriList() {
        return uriList;
    }

    public void setUriList(List<String> uriList) {
        this.uriList = uriList;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
}
