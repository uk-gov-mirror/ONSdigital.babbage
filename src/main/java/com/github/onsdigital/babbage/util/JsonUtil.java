package com.github.onsdigital.babbage.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bren on 12/08/15.
 */
public class JsonUtil {

    /**
     * Converts json string to Map<String,Object>
     *
     * @return
     */
    public static Map<String, Object> deserialiseObject(String data) throws IOException {
        return convert(data);
    }

    /**
     * Converts json stream to Map<String,Object>
     *
     * @return
     */
    public static Map<String, Object> deserialiseObject(InputStream stream) throws IOException {
        return convert(stream);
    }

    public static List<Map<String, Object>> deserialiseArray(String data) throws IOException {
        return convertArray(data);
    }
    public static List<Map<String, Object>> deserialiseArray(InputStream stream) throws IOException {
        return convertArray(stream);
    }


    private static Map<String, Object> convert(String data) throws IOException {
        if (data == null) {
            return Collections.emptyMap();
        }
        return new ObjectMapper().readValue(data, new TypeReference<HashMap<String, Object>>() {
        });
    }

    private static Map<String, Object> convert(InputStream stream) throws IOException {
        if (stream == null) {
            return Collections.emptyMap();
        }
        return new ObjectMapper().readValue(stream, new TypeReference<HashMap<String, Object>>() {
        });
    }

    private static List<Map<String, Object>> convertArray(String data) throws IOException {
        if (data == null) {
            return Collections.emptyList();
        }
        return new ObjectMapper().readValue(data, new TypeReference<List<HashMap<String, Object>>>() {
        });
    }

    private static List<Map<String, Object>> convertArray(InputStream stream) throws IOException {
        if (stream == null) {
            return Collections.emptyList();
        }
        return new ObjectMapper().readValue(stream, new TypeReference<List<HashMap<String, Object>>>() {
        });
    }


}
