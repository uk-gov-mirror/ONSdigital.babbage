package com.github.onsdigital.babbage.logging;

import ch.qos.logback.classic.Level;
import com.github.onsdigital.logging.builder.LogMessageBuilder;

public class LogBuilder extends LogMessageBuilder {

    public static LogBuilder Log() {
        return new LogBuilder("");
    }

    protected LogBuilder(String eventDescription) {
        super(eventDescription);
    }

    @Override
    public String getLoggerName() {
        return "babbage";
    }

    public void info(String message) {
        logLevel = Level.INFO;
        description = message;
        log();
    }

    public LogBuilder parameter(String key, Object value) {
        super.addParameter(key, value);
        return this;
    }
}
