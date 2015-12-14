package com.github.onsdigital.babbage.cache;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.util.ElasticSearchUtils;
import com.github.onsdigital.babbage.util.RequestUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.github.onsdigital.babbage.configuration.Configuration.CACHE.isCacheEnabled;

/**
 * Created by bren on 09/07/15.
 */
public class BabbageCache {

    private static BabbageCache instance = new BabbageCache();
    private static Cache contentCache;
    private static Cache resourceCache;
    private static ElasticSearchUtils elasticSearchUtils;
    private static final String SEARCH_INDEX = "publishdates";

    private BabbageCache() {
    }

    /**
     * Gets cached response from upcoming cache if available, if not will try general cache, if response not found in both cache will use load function to load and cache it to general cache
     *
     * @param requestedUri
     * @param parameters
     * @param loader
     * @return
     * @throws Exception
     */
    public ContentResponse getContent(String requestedUri, Map<String, String[]> parameters, Callable<ContentResponse> loader) throws Exception {
        return getFromCache(requestedUri, parameters, loader, contentCache);
    }

    public ContentResponse getResource(String requestedUri, Map<String, String[]> parameters, Callable<ContentResponse> loader) throws Exception {
        return getFromCache(requestedUri, parameters, loader, resourceCache);
    }

    private ContentResponse getFromCache(String requestedUri, Map<String, String[]> parameters, Callable<ContentResponse> loader, Cache cache) throws Exception {
        if (!isCacheEnabled()) {
            return loader.call();
        }
        String key = requestedUri + "?" + RequestUtil.toQueryString(parameters);
        return (ContentResponse) get(cache, key,
                () -> { //load if not found in cache
                    ContentResponse response = loader.call();

                    Date publishDate = (Date) get(upcomingPublishDates, key, null);
                    if (publishDate != null) {
                        response.setExpireDate(publishDate);
                    } else {
                        response.setMaxAge(Configuration.CACHE.getDefaultCacheTime());
                    }
                    Element element = new Element(key, response);
                    element.setTimeToLive(response.getMaxAge());
                    cache.put(element);
                    return response;
                }
        );
    }

    //executes chain function if not found in cache
    private Object get(Cache cache, String key, Callable chain) throws Exception {
        Element element = cache.get(key);
        if (element == null) {
            if (chain != null) {
                return chain.call();
            }
            return null;
        }
        System.out.println(cache.getName() + " HIT: " + key);
        return element.getObjectValue();
    }

    public static BabbageCache getInstance() {
        return instance;
    }

    public static void init() throws IOException {
        if (isCacheEnabled()) {
            System.out.println("Initializing caches");
            CacheManager cacheManager = CacheManager.create(Configuration.CACHE.getCacheConFig());
            contentCache = cacheManager.getCache(Configuration.CACHE.getContentCacheName());
            resourceCache = cacheManager.getCache(Configuration.CACHE.getResourceCacheName());
            initPublishDates();
        }
    }

    private static void initPublishDates() throws IOException {
        elasticSearchUtils = new ElasticSearchUtils(SearchService.getClient());
        if(!elasticSearchUtils.isIndexAvailable(SEARCH_INDEX)) {
            elasticSearchUtils.createIndex(SEARCH_INDEX);
        }




        ImmutableSettings.settingsBuilder().put
    }

    private Settings buildIndexSettings() {
        new Hash

        ImmutableSettings.settingsBuilder().
    }
}
