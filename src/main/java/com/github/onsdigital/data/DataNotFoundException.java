package com.github.onsdigital.data;

import com.github.onsdigital.babbage.error.ResourceNotFoundException;

/**
 * Created by bren on 28/05/15.
 */
public class DataNotFoundException extends ResourceNotFoundException {

    public DataNotFoundException(String uri) {
        super("Data not found under " + uri);
    }
}

