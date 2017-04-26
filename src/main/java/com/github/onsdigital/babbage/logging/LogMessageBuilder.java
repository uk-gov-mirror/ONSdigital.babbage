package com.github.onsdigital.babbage.logging;

import ch.qos.logback.classic.Level;
import com.github.onsdigital.babbage.error.BabbageException;
import com.github.onsdigital.babbage.error.BadRequestException;
import com.github.onsdigital.babbage.error.InternalServerErrorException;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;

/**
 * Babbage Implementation of abstract LogMessageBuilder
 */
public class LogMessageBuilder extends com.github.onsdigital.logging.builder.LogMessageBuilder {

    public static final String LOG_NAME = "com.github.onsdigital.babbage";

    public LogMessageBuilder(String eventDescription) {
        super(eventDescription);
    }

    public LogMessageBuilder(String description, Level logLevel) {
        super(description, logLevel);
    }

    public LogMessageBuilder(Throwable t, Level level, String description) {
        super(t, level, description);
    }

    public LogMessageBuilder(Throwable t, String description) {
        super(t, description);
    }

    public BabbageException logAndCreateException(int statusCode, Throwable cause) {
        addParameter("details", cause.getMessage());
        log();
        switch (statusCode) {
            case 400:
                return new BadRequestException(cause.getMessage());
            case 404:
                return new ResourceNotFoundException(cause.getMessage());
            default:
                return new InternalServerErrorException(cause.getMessage(), cause);
        }
    }

    @Override
    public String getLoggerName() {
        return LOG_NAME;
    }
}
