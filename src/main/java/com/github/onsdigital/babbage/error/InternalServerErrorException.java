package com.github.onsdigital.babbage.error;

/**
 * Created by dave on 13/04/2017.
 */
public class InternalServerErrorException extends BabbageException {

    public InternalServerErrorException(String message, Throwable cause) {
        super(500, message, cause);
    }
}
