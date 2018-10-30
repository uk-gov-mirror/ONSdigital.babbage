package com.github.onsdigital.babbage.search.external.requests.spellcheck;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.onsdigital.babbage.search.external.SearchEndpoints;
import com.github.onsdigital.babbage.search.external.requests.base.AbstractSearchRequest;
import com.github.onsdigital.babbage.search.external.requests.spellcheck.models.SpellingCorrection;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpScheme;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpellCheckRequest extends AbstractSearchRequest<List<SpellingCorrection>> {

    private final String searchTerm;
    private final URIBuilder uriBuilder;

    public SpellCheckRequest(String searchTerm) {
        super(new TypeReference<List<SpellingCorrection>>() {});
        this.searchTerm = searchTerm;

        this.uriBuilder = new URIBuilder()
                .setScheme(HttpScheme.HTTP.asString())
                .setHost(HOST)
                .setPath(SearchEndpoints.SPELLING.getEndpoint())
                .addParameter("q", this.searchTerm);
    }

    @Override
    public URIBuilder targetUri() {
        return this.uriBuilder;
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
        Map<String, String> correctionMap = spellingCorrections.stream()
                .filter(correction -> correction.getProbability() > threshold)
                .collect(Collectors.toMap(SpellingCorrection::getInputToken, SpellingCorrection::getCorrection));

        List<String> inputTokens = Arrays.asList(inputString.split(" "));

        return inputTokens.stream()
               .filter(token -> correctionMap.containsKey(token))
               .map(token -> correctionMap.get(token))
               .collect(Collectors.joining(" "));
    }
}
