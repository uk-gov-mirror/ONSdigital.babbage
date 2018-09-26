package com.github.onsdigital.babbage.search.external.requests.suggest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.onsdigital.babbage.search.external.SearchEndpoints;
import com.github.onsdigital.babbage.search.external.requests.base.AbstractSearchRequest;
import com.github.onsdigital.babbage.search.external.requests.suggest.models.SpellingCorrection;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpScheme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellCheckRequest extends AbstractSearchRequest<List<SpellingCorrection>> {

    private final String searchTerm;

    public SpellCheckRequest(String searchTerm) {
        super(new TypeReference<List<SpellingCorrection>>() {});
        this.searchTerm = searchTerm;
    }

    @Override
    public URIBuilder targetUri() {
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme(HttpScheme.HTTP.asString())
                .setHost(HOST)
                .setPath(SearchEndpoints.SPELLING.getEndpoint())
                .addParameter("q", this.searchTerm);

        return uriBuilder;
    }

    @Override
    protected ContentResponse getContentResponse() throws Exception {
        return super.get().send();
    }

    /**
     * Builds a single suggestion string from a list of possible suggestions
     * @param inputString
     * @param spellingCorrections
     * @return
     */
    public static String buildSuggestedCorrection(String inputString, List<SpellingCorrection> spellingCorrections, float threshold) {
        StringBuilder sb = new StringBuilder();
        String[] inputTokens = inputString.split(" ");

        // Build a map of input tokens to corrections
        Map<String, String> correctionMap = new HashMap<>();
        for (SpellingCorrection spellingCorrection : spellingCorrections) {
            if (spellingCorrection.getProbability() > threshold) {
                correctionMap.put(spellingCorrection.getInputToken(), spellingCorrection.getCorrection());
            }
        }

        // Build up suggested string
        for (String inputToken : inputTokens) {
            if (correctionMap.containsKey(inputToken)) {
                sb.append(correctionMap.get(inputToken));
            } else {
                // Just add the input token
                sb.append(inputToken);
            }
            sb.append(" ");
        }

        return sb.toString().trim();
    }
}
