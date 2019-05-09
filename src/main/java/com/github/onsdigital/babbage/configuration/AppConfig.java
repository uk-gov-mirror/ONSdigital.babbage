package com.github.onsdigital.babbage.configuration;

import java.util.Map;

@FunctionalInterface
public interface AppConfig {

    Map<String, Object> getConfig();
}
