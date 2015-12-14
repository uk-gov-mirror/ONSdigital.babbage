package com.github.onsdigital.babbage;

import com.github.davidcarboni.restolino.framework.Startup;
import com.github.onsdigital.babbage.cache.BabbageCache;

/**
 * Created by bren on 13/12/15.
 *
 * Startup steps for Babbage
 */
public class Init implements Startup {

    @Override
    public void init() {
        BabbageCache.init();
    }
}
