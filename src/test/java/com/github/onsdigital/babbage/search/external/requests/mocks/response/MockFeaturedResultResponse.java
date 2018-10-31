package com.github.onsdigital.babbage.search.external.requests.mocks.response;

import com.github.onsdigital.babbage.search.external.requests.mocks.json.MockContentJson;
import com.github.onsdigital.babbage.search.external.requests.mocks.json.MockSearchJson;
import org.apache.http.StatusLine;

public class MockFeaturedResultResponse extends MockSearchResponse {

    private static StatusLine STATUS_LINE = new MockStatusLine(200);
    private static MockSearchJson MOCK_SEARCH_JSON = new MockContentJson();

    public MockFeaturedResultResponse() {
        super(STATUS_LINE, MOCK_SEARCH_JSON);
    }
}
