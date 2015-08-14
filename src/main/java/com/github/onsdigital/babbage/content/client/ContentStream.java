package com.github.onsdigital.babbage.content.client;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by bren on 07/08/15.
 */
public class ContentStream  implements Closeable {

    private CloseableHttpResponse response;


    ContentStream(CloseableHttpResponse response) {
        this.response = response;
    }

    /**
     * Maximum age of content in minutes
     *
     * @return
     */
    public String getCacheControl() {
        return response.getHeaders("cache-control").toString();

    }

    public String getMimeType() {
        return getContentType().getMimeType();
    }

    public Charset getCharset() {
        return getContentType().getCharset();
    }

    public InputStream getDataStream() throws IOException {
        return response.getEntity().getContent();
    }

    public String getAsString() throws IOException {
        return EntityUtils.toString(response.getEntity());
    }

    private ContentType getContentType() {
        return ContentType.getLenient(response.getEntity());
    }

    /**
     *
     * @return size in bytes
     */
    public long getSize() {
        return response.getEntity().getContentLength();
    }

    public String getName() {
        Header[] contentDisposition = response.getHeaders("Content-Disposition");
        if (contentDisposition != null && contentDisposition.length > 0) {
            HeaderElement[] elements = contentDisposition[0].getElements();
            if (elements != null && elements.length>0) {
                NameValuePair filename = elements[0].getParameterByName("filename");
                return filename == null ? null : filename.getValue();

            }
        }
        return null;
    }


    @Override
    public void close() throws IOException {
        if (response != null) {
            response.close();
        }
    }






}
