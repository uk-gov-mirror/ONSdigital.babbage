package com.github.onsdigital.babbage.search.external.requests.suggest.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SpellCheckResult extends HashMap<String, SpellCheckResult.Correction> {

    private static final String WHITESPACE = " ";

    public boolean correctionExists(String key) {
        return this.containsKey(key) && (null != this.get(key).getCorrection() && !this.get(key).getCorrection().isEmpty());
    }

    public SpellCheckResult.Correction getCorrectionForKey(String key) {
        if (this.correctionExists(key)) {
            return this.get(key);
        }
        return null;
    }

    /**
     * Returns suggested correction as a singleton list (for babbage compatibility)
     * @return
     */
    public String getSuggestedCorrection() {
        StringBuilder spellingStringBuilder = new StringBuilder();

        Set<String> keySet = this.keySet();
        Iterator<String> it = keySet.iterator();
        while (it.hasNext()) {
            String key = it.next();
            SpellCheckResult.Correction correction = this.getCorrectionForKey(key);
            if (null != correction) {
                spellingStringBuilder.append(correction.getCorrection());
                if (it.hasNext()) {
                    spellingStringBuilder.append(WHITESPACE);
                }
            }
        }
        String suggestion = spellingStringBuilder.toString();

        return suggestion;
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
