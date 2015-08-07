package com.github.onsdigital.cache;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.request.response.BabbageResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by bren on 09/07/15.
 */
public class BabbageResponseCache {

    Cache<String,BabbageResponse> cache;

    public BabbageResponseCache(long maxSize) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(maxSize).expireAfterWrite(Configuration.GENERAL.getGlobalCacheTimeout(), TimeUnit.MINUTES)
                .build();
    }


    public BabbageResponse get(String uri, Callable<BabbageResponse> loader) throws ExecutionException {
        return cache.get(uri, loader);
    }
}
