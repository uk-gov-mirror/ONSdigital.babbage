package com.github.onsdigital.babbage.error;

/**
 * Created by bren on 19/11/15.
 */
public abstract class BabbageException extends RuntimeException {

    private final int statusCode;

    public BabbageException(int statusCode) {
        this.statusCode = statusCode;
    }

    public BabbageException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public BabbageException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
