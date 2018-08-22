package com.github.onsdigital.babbage.search.external.requests.suggest;

import com.github.onsdigital.babbage.search.external.SearchEndpoints;
import com.github.onsdigital.babbage.search.external.requests.base.AbstractSearchRequest;
import com.github.onsdigital.babbage.search.external.requests.suggest.models.SpellCheckResult;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpScheme;

public class SpellCheckRequest extends AbstractSearchRequest<SpellCheckResult> {

    private final String searchTerm;

    public SpellCheckRequest(String searchTerm) {
        super(SpellCheckResult.class);
        this.searchTerm = searchTerm;
    }

    @Override
    public String targetUri() {
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme(HttpScheme.HTTP.asString())
                .setHost(HOST)
                .setPath(SearchEndpoints.SPELLING.getEndpoint())
                .addParameter("q", this.searchTerm);

        return uriBuilder.toString();
    }

    @Override
    protected ContentResponse getContentResponse() throws Exception {
        return super.get().send();
    }
}
