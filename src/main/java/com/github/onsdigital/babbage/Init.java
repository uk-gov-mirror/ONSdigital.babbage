package com.github.onsdigital.babbage;

import com.github.davidcarboni.restolino.framework.Startup;
import com.github.onsdigital.babbage.publishing.PublishingManager;
import com.github.onsdigital.babbage.search.ElasticSearchClient;
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
            ElasticSearchClient.init();
            PublishingManager.init();
        } catch (Exception e) {
            System.err.println("!!!!Failed initializing publish dates index for caching");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
