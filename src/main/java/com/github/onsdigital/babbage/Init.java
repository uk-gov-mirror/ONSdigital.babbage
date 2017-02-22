package com.github.onsdigital.babbage;

import com.github.davidcarboni.restolino.framework.Startup;
import com.github.onsdigital.babbage.logging.LogMessageBuilder;
import com.github.onsdigital.babbage.publishing.PublishingManager;
import com.github.onsdigital.babbage.search.ElasticSearchClient;
import com.github.onsdigital.babbage.util.RequestUtil;

import static com.github.onsdigital.babbage.logging.Log.debug;

/**
 * Created by bren on 13/12/15.
 * <p/>
 * Startup steps for Babbage
 */
public class Init implements Startup {

    @Override
    public void init() {
        try {
            debug("SITE_DOMAIN=" + RequestUtil.SITE_DOMAIN);
            ElasticSearchClient.init();
            PublishingManager.init();
        } catch (Exception e) {
            System.err.println("!!!!Failed initializing publish dates index for caching");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
