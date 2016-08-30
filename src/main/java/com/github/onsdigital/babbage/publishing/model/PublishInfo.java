package com.github.onsdigital.babbage.publishing.model;

import com.github.onsdigital.babbage.search.model.ContentType;

import java.util.Date;

/**
 * Created by bren on 16/12/15.
 */
public class PublishInfo {

    private String uri;
    private String collectionId;
    private Date publishDate;
    private FilePublishType filePublishType;
    private ContentType contentType;

    public PublishInfo(String uri, String collectionId, Date publishDate, FilePublishType filePublishType, ContentType contentType) {
        this.uri = uri;
        this.collectionId = collectionId;
        this.publishDate = publishDate;
        this.filePublishType = filePublishType;
        this.contentType = contentType;
    }

    public PublishInfo(String uri, String collectionId, Date publishDate, FilePublishType filePublishType) {
        this.uri = uri;
        this.collectionId = collectionId;
        this.publishDate = publishDate;
        this.filePublishType = filePublishType;
    }

    public FilePublishType getFilePublishType() {
        return filePublishType;
    }

    public void setFilePublishType(FilePublishType filePublishType) {
        this.filePublishType = filePublishType;
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

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }
}
