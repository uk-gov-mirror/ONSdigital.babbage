package com.github.onsdigital.babbage;

import com.github.davidcarboni.restolino.framework.Startup;
import com.github.onsdigital.babbage.cache.BabbageCache;
import com.github.onsdigital.babbage.search.SearchService;

import java.io.IOException;

/**
 * Created by bren on 13/12/15.
 * <p/>
 * Startup steps for Babbage
 */
public class Init implements Startup {

    @Override
    public void init() {
        try {
            SearchService.init();
            BabbageCache.init();
        } catch (IOException e) {
            System.err.println("!!!!Failed initializing Babbage");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
