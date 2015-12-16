package com.github.onsdigital.babbage.publishing.model;

/**
 * Created by bren on 16/12/15.
 */
public class ResponseMessage {
    private String messsage;

    public ResponseMessage(String messsage) {
        this.messsage = messsage;
    }

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }
}
