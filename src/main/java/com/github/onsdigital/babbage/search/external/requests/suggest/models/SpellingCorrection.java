package com.github.onsdigital.babbage.search.external.requests.suggest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpellingCorrection {

    @JsonProperty("input_token")
    private String inputToken;
    private String correction;
    private float probability;

    private SpellingCorrection() {
        // For Jackson
    }

    public SpellingCorrection(String input, String correction, float probability) {
        this.inputToken = input;
        this.correction = correction;
        this.probability = probability;
    }

    @JsonProperty("input_token")
    public String getInputToken() {
        return inputToken;
    }

    public String getCorrection() {
        return correction;
    }

    public float getProbability() {
        return probability;
    }
}
