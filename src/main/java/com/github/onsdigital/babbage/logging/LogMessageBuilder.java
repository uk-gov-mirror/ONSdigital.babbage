package com.github.onsdigital.babbage.logging;

import ch.qos.logback.classic.Level;

/**
 * Babbage Implementation of abstract LogMessageBuilder
 */
public class LogMessageBuilder extends com.github.onsdigital.logging.builder.LogMessageBuilder {

    public static final String LOG_NAME = "com.github.onsdigital.babbage";

    public LogMessageBuilder(String description, Level logLevel) {
        super(description, logLevel);
    }

    @Override
    public String getLoggerName() {
        return LOG_NAME;
    }
}
