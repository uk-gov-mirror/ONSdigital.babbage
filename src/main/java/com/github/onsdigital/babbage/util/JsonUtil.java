package com.github.onsdigital.babbage.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bren on 12/08/15.
 */
public class JsonUtil {

    /**
     * Converts json object string to Map<String,Object>
     *
     * @return
     */
    public static Map<String, Object> toMap(String data) throws IOException {
        return fromJson(data, mapType());
    }

    /**
     * Converts json object stream to Map<String,Object>
     *
     * @return
     */
    public static Map<String, Object> toMap(InputStream stream) throws IOException {
        return fromJson(stream, mapType());
    }

    /**
     * Converts json array to list
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static List<Map<String, Object>> toList(String data) throws IOException {
        return fromJson(data, listType());
    }

    public static List<Map<String, Object>> toList(InputStream stream) throws IOException {
        return fromJson(stream, listType());
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        if (json == null) {
            return null;
        }
        return objectMapper().readValue(json, clazz);
    }

    public static <T> T fromJson(InputStream stream, Class<T> clazz) throws IOException {
        if (stream == null) {
            return null;
        }
        return objectMapper().readValue(stream, clazz);
    }


    private static <T> T fromJson(String json, TypeReference type) throws IOException {
        if (json == null) {
            return null;
        }
        return objectMapper().readValue(json, type);
    }

    private static <T> T fromJson(InputStream jsonStream, TypeReference type) throws IOException {
        if (jsonStream == null) {
            return null;
        }
        return objectMapper().readValue(jsonStream, type);
    }

    private static TypeReference<HashMap<String, Object>> mapType() {
        return new TypeReference<HashMap<String, Object>>() {
        };
    }

    private static TypeReference<List<HashMap<String, Object>>> listType() {
        return new TypeReference<List<HashMap<String, Object>>>() {};
    }

    private static ObjectMapper objectMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

}
