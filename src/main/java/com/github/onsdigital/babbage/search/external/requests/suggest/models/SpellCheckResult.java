package com.github.onsdigital.babbage.search.external.requests.suggest.models;

import java.util.HashMap;

public class SpellCheckResult extends HashMap<String, SpellCheckResult.Correction> {

    public boolean correctionExists(String key) {
        return this.containsKey(key) && (null != this.get(key).getCorrection() && !this.get(key).getCorrection().isEmpty());
    }

    public SpellCheckResult.Correction getCorrectionForKey(String key) {
        if (this.correctionExists(key)) {
            return this.get(key);
        }
        return null;
    }

    public static class Correction {

        private String correction;
        private Float probability;

        private Correction() {
            // For Jackson
        }

        public Correction(String correction, Float probability) {
            this.correction = correction;
            this.probability = probability;
        }

        public String getCorrection() {
            return correction;
        }

        public Float getProbability() {
            return probability;
        }
    }

}
