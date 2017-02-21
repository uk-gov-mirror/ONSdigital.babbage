package com.github.onsdigital.babbage.logging;

import ch.qos.logback.classic.Level;

/**
 * High level static interface to logging in babbage.
 */
public class Log {

    /**
     * Log the given messsage to a default debug log level.
     * @param message
     */
    public static void debug(String message) {
        new LogMessageBuilder(message, Level.DEBUG).log();
    }

    /**
     * Helper method to provide a log message builder for the debug log level.
     * @param message
     * @return
     */
    public static LogMessageBuilder buildDebug(String message) {
        return build(message, Level.DEBUG);
    }

    /**
     * Create a log message builder using the given message and log level.
     * @param message
     * @param level
     * @return
     */
    public static LogMessageBuilder build(String message, Level level) {
        return new LogMessageBuilder(message, level);
    }
}