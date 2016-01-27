package com.github.onsdigital.babbage.publishing.model;

import com.github.onsdigital.babbage.configuration.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 */
public class PublishNotification {
    private String key;
    private String collectionId;
    private List<String> uriList;
    private String publishDate;

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

    public String getPublishDate() {
        return publishDate;
    }

    public Date getDate(){
        try {
            return new SimpleDateFormat(Configuration.CONTENT_SERVICE.getDefaultContentDatePattern()).parse(publishDate);
        } catch (ParseException e) {
            System.err.println("Warning!!!!!!!! Publish date for publish notification is invalid, can not parse to date");
            e.printStackTrace();
            return null;
        }
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
}
