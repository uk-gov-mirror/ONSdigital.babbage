package com.github.onsdigital.babbage.search.external.requests.mocks.response;

import com.github.onsdigital.babbage.search.external.requests.mocks.json.MockSearchJson;
import com.github.onsdigital.babbage.search.external.requests.mocks.json.MockTypeCountsJson;
import org.apache.http.StatusLine;

public class MockTypeCountsResponse extends MockSearchResponse {

    private static StatusLine STATUS_LINE = new MockStatusLine(200);
    private static MockSearchJson MOCK_SEARCH_JSON = new MockTypeCountsJson();


    public MockTypeCountsResponse() {
        super(STATUS_LINE, MOCK_SEARCH_JSON);
    }
}
