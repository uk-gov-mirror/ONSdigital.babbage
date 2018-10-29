package com.github.onsdigital.babbage;

import com.github.davidcarboni.restolino.framework.Startup;
import com.github.onsdigital.babbage.configuration.ApplicationConfiguration;
import com.github.onsdigital.babbage.logging.LogBuilder;
import com.github.onsdigital.babbage.publishing.PublishingManager;
import com.github.onsdigital.babbage.search.ElasticSearchClient;
import com.github.onsdigital.babbage.search.external.SearchClient;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.babbage.logging.LogBuilder.logEvent;

/**
 * Created by bren on 13/12/15.
 * <p/>
 * Startup steps for Babbage
 */
public class Init implements Startup {

    @Override
    public void init() {
        LogBuilder.logEvent().info("starting babbage");

        ApplicationConfiguration.init();

        try {
            ElasticSearchClient.init();
            PublishingManager.init();

            if (appConfig().externalSearch().isEnabled()) {
                // Initialise HTTP client for external search service
                SearchClient.getInstance();
            }
        } catch (Exception e) {
            logEvent(e).error("error initializing publish dates index for caching");
            System.exit(1);
        }
    }
}
