package com.github.onsdigital.babbage.error;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by bren on 19/11/15.
 */
public class BadRequestException extends BabbageException {

    private final static int BAD_REQUEST = HttpServletResponse.SC_BAD_REQUEST;

    public BadRequestException(String message) {
        super(BAD_REQUEST, message);
    }
}
