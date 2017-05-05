package com.github.onsdigital.babbage.util.json;

/**
 * Created by bren on 14/08/15.
 */
public class InvalidJsonException extends RuntimeException  {

    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
