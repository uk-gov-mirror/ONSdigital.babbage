package com.github.onsdigital.babbage.util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by guidof on 21/03/17.
 */
public class ListUtil {

    public static <T> T nullSafeGet(final List<T> list, final int idx) {
        T returnVal = null;
        if (null != list && idx < list.size()) {
            returnVal = list.get(idx);
        }
        return returnVal;
    }

    public static <T> void nullSafeForEach(Iterable<T> it, Consumer<? super T> action) {
        if (null != it) {
            it.forEach(action);
        }
    }

    public static boolean isNotEmpty(final List list) {
        return CollectionUtils.isNotEmpty(list);
    }
}
