package com.github.onsdigital.babbage.content.proxy;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bren on 07/08/15.
 *
 * Proxies requests to babbage to content service passing all query parameters to content service.
 *
 * Content proxy does not pass any post parameters to content service
 *
 */
public class ContentServiceProxy {

    private static ContentServiceProxy instance;


    public ContentServiceProxy getInstance() {

        if (instance == null) {
            synchronized (ContentServiceProxy.class) {
                if (instance == null) {
                    instance = new ContentServiceProxy();
                }
            }
        }
        return instance;
    }

    public void proxy(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException, ContentReadException {
        ContentStream stream =  getContent(uri, request);
    }

    private ContentStream getContent(String uri, HttpServletRequest request) throws IOException, ContentReadException {
        ContentClient.getInstance().getContentStream(uri, );
    }

}
