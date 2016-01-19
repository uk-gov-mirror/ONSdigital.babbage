package com.github.onsdigital.babbage.api.util;

import java.util.LinkedHashMap;

/**
 * Created by bren on 19/01/16.
 */
public class ListUtils {

    public static LinkedHashMap<String, Object> getBaseListTemplate(String listType) {
        LinkedHashMap<String, Object> baseData = new LinkedHashMap<>();
        baseData.put("type", "list");
        baseData.put("listType", listType.toLowerCase());
        return baseData;
    }

}
