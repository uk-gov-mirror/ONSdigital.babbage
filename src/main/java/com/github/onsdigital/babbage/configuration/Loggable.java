package com.github.onsdigital.babbage.configuration;

/**
 * A configuration class that can log out its configuration values.
 */
@FunctionalInterface
interface Loggable {
    void logConfiguration();
}
