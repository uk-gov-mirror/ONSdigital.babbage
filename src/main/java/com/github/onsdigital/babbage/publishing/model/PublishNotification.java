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
    private List<String> urisToUpdate;
    private List<String> urisToDelete;
    private String publishDate;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getUrisToUpdate() {
        return urisToUpdate;
    }

    public Date getDate(){
        if (publishDate == null) return null;

        try {
            return new SimpleDateFormat(Configuration.CONTENT_SERVICE.getDefaultContentDatePattern()).parse(publishDate);
        } catch (ParseException e) {
            System.err.println("Warning!!!!!!!! Publish date for publish notification is invalid, can not parse to date");
            e.printStackTrace();
            return null;
        }
    }

    public void setUrisToUpdate(List<String> urisToUpdate) {
        this.urisToUpdate = urisToUpdate;
    }

    public List<String> getUrisToDelete() {
        return urisToDelete;
    }

    public void setUrisToDelete(List<String> urisToDelete) {
        this.urisToDelete = urisToDelete;
    }

    public String getPublishDate() {
        return publishDate;
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
