package com.github.onsdigital.babbage.logging;

import ch.qos.logback.classic.Level;

/**
 * Implementation of abstract LogMessageBuilder
 */
public class LogMessageBuilder extends com.github.onsdigital.logging.builder.LogMessageBuilder {

    public static final String LOG_NAME = "Babbage";

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

    @Override
    public String getLoggerName() {
        return LOG_NAME;
    }
}
