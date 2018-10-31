package com.github.onsdigital.babbage.search.external.requests.search.headers;

import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.eclipse.jetty.http.HttpHeader;

public class JsonContentTypeHeader extends BasicHeader {

    private static final String KEY = HttpHeader.CONTENT_TYPE.asString();
    private static final String MIME_TYPE = ContentType.APPLICATION_JSON.getMimeType();

    public JsonContentTypeHeader() {
        super(KEY, MIME_TYPE);
    }
}
