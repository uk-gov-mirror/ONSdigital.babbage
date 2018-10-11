package com.github.onsdigital.babbage.publishing.model;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;

/**
 */
public class PublishNotification {
    private String key;
    private String collectionId;
    private List<String> urisToUpdate;
    private List<ContentDetail> urisToDelete;
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

    public Date getDate() {
        if (publishDate == null) return null;

        try {
            return appConfig()
                    .contentAPI()
                    .defaultContentDateFormat()
                    .parse(publishDate);
        } catch (ParseException e) {
            System.err.println("Warning!!!!!!!! Publish date for publish notification is invalid, can not parse to date");
            e.printStackTrace();
            return null;
        }
    }

    public void setUrisToUpdate(List<String> urisToUpdate) {
        this.urisToUpdate = urisToUpdate;
    }

    public List<ContentDetail> getUrisToDelete() {
        return urisToDelete;
    }

    public void setUrisToDelete(List<ContentDetail> urisToDelete) {
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
