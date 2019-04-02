package com.github.onsdigital.babbage.search.external.requests.base;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;

/**
 * Class for adding Shutdown hooks for search threads
 */
public class ShutdownThread extends Thread {

    private final SearchClosable searchClosable;

    public ShutdownThread(SearchClosable searchClosable) {
        this.searchClosable = searchClosable;
    }

    @Override
    public void run() {
        try {
            this.searchClosable.close();
        } catch (Exception e) {
            error().exception(e).log("error while attempting to run shutdown task for object");
        }
    }

}
