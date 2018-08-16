package com.github.onsdigital.babbage.search.external.requests.base;

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
            System.out.println(String.format("Unable to close object: %s", this.searchClosable));
            e.printStackTrace();
        }
    }

}
