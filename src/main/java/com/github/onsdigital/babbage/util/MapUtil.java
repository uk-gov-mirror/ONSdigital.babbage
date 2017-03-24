package com.github.onsdigital.babbage.util;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by guidof on 21/03/17.
 */
public class MapUtil {

    public static <T> T nullSafeGet(final Map<String, T> map, final String key) {
        T returnVal = null;
        if (null != map) {
            returnVal =  map.get(key);
        }
        return returnVal;

    }
    public static <T> T nullSafeGet(final Map<String, ?> map, Class<T> returnClazz, final String key) {
        T returnVal = null;
        if (null != map) {
            //noinspection unchecked
            returnVal =  (T) map.get(key);
        }
        return returnVal;

    }


    public static <K, V> void nullSafeForEach(Map<K, V> map, Consumer<Map.Entry<K, V>> action) {
        if (null != map) {
            map.entrySet()
               .forEach(action);
        }
    }
}
