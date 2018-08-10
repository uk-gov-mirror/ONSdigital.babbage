package com.github.onsdigital.babbage;

import com.github.davidcarboni.restolino.framework.Startup;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.publishing.PublishingManager;
import com.github.onsdigital.babbage.search.ElasticSearchClient;
import com.github.onsdigital.babbage.search.external.SearchClient;

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

            if (Configuration.SEARCH_SERVICE.EXTERNAL_SEARCH_ENABLED) {
                // Initialise HTTP client for external search service
                SearchClient.init();
            }
        } catch (Exception e) {
            System.err.println("!!!!Failed initializing publish dates index for caching");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
