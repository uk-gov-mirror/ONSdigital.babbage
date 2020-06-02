package com.github.onsdigital.babbage.util.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

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

    public static String toJson(Object object) throws InvalidJsonException {
        try {
            return objectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException("Failed parsing to json", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        if(isEmpty(json)) {
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
        if(StringUtils.isEmpty(json)) {
            return null;
        }
        return (T) objectMapper().readValue(json, type);
    }

    private static <T> T fromJson(InputStream jsonStream, TypeReference type) throws IOException {
        if (jsonStream == null) {
            return null;
        }
        return (T) objectMapper().readValue(jsonStream, type);
    }

    private static TypeReference<LinkedHashMap<String, Object>> mapType() {
        return new TypeReference<LinkedHashMap<String, Object>>() {
        };
    }

    private static TypeReference<List<LinkedHashMap<String, Object>>> listType() {
        return new TypeReference<List<LinkedHashMap<String, Object>>>() {};
    }

    private static ObjectMapper objectMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

}
