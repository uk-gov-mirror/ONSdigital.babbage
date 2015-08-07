package com.github.onsdigital.babbage.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by bren on 23/07/15.
 * <p/>
 * Used to bind data to thread to be used around the app. Data is bound as key value pairs
 */
public final class ThreadContext {

    private ThreadContext() {
    }

    private final static ThreadLocal<Map<String, Object>> threadContext = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    };


    /**
     * add data to current thread. will overwrite if key already exists
     * @param key
     * @param data
     */
    public static void addData(String key, Object data) {
        Map<String, Object> params = threadContext.get();
        params.put(key, data);
    }

    /**
     * Clear all thread data
     */
    public static void clear() {
        threadContext.get().clear();
    }

    /**
     * Read data bound to current thread
     *
     * @param key
     * @return
     */
    public static Object getData(String key) {
        Map<String, Object> params = threadContext.get();
        return params.get(key);
    }

    public static Iterator<Map.Entry<String, Object>> iterate() {
        return threadContext.get().entrySet().iterator();
    }
}
