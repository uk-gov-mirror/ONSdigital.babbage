package com.github.onsdigital.babbage.search.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.util.ListUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.babbage.util.MapUtil.nullSafeGet;

/**
 * Created by guidof on 22/03/17.
 */
public class TimeSeriesResultFactory {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static TimeSeriesResult getInstance(byte[] timeSeriesResponse) throws IOException {
        final Map hits = nullSafeGet(MAPPER.readValue(timeSeriesResponse, Map.class), "hits");


        List<Map<String, String>> results = nullSafeGet((Map<String, List<Map<String, String>>>) hits, "hits");
        final Map<String, ?> firstEntry = ListUtil.nullSafeGet(results, 0);

        TimeSeriesResult r = null;
        if (null != firstEntry) {
            r = new TimeSeriesResult();
            r.setId(nullSafeGet(firstEntry, String.class, "_id"));
            r.setIndex(nullSafeGet(firstEntry, String.class,"_index"));
            r.setType(nullSafeGet(firstEntry, String.class,"_type"));

            final Map source = nullSafeGet(firstEntry, Map.class, "_source");
            r.setUri(nullSafeGet(source, String.class, "uri"));
        }

        return r;
    }
}
