package com.github.onsdigital.babbage.search.external.requests.base;

/**
 * Simple interface for ShutdownThread
 */
public interface SearchClosable {

    void close() throws Exception;

}
