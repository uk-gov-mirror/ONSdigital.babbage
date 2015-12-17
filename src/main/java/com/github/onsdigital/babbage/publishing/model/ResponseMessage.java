package com.github.onsdigital.babbage.publishing.model;

/**
 * Created by bren on 16/12/15.
 */
public class ResponseMessage {
    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
