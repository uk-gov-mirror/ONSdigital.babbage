package com.github.onsdigital.babbage.search.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.search.external.requests.suggest.models.SpellCheckResult;

import java.io.IOException;

public class TestSuggestResponseUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static SpellCheckResult getTestResult() throws IOException {
        String json = "{\n" +
                "\"rpo\": {\n" +
                "\"correction\": \"rpi\",\n" +
                "\"probability\": 0.8609125179\n" +
                "}\n" +
                "}";

        SpellCheckResult result = MAPPER.readValue(json, SpellCheckResult.class);
        return result;
    }

}
