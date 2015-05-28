package com.github.onsdigital.data;

/**
 * Created by bren on 28/05/15.
 */
public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(String uri) {
        super("Data not found under " + uri);
    }
}

