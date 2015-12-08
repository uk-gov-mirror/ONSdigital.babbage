package com.github.onsdigital.babbage.error;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by bren on 29/05/15.
 * <p/>
 * All exceptions extending ResourceNotFoundException will result in http 404 sent to the client
 */
public class ResourceNotFoundException extends BabbageException {

    private final static int NOT_FOUND = HttpServletResponse.SC_NOT_FOUND;

    public ResourceNotFoundException(String message) {
        super(NOT_FOUND, message);
    }

    public ResourceNotFoundException() {
        super(NOT_FOUND);
    }

}
