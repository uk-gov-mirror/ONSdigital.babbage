package com.github.onsdigital.babbage.search.external.requests.search.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.search.external.MockedFeaturedResultResponse;
import com.github.onsdigital.babbage.search.external.MockedHttpRequest;
import com.github.onsdigital.babbage.search.external.SearchClient;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class FeaturedResultQueryTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String searchTerm = "Who ya gonna call?";
    private final ListType listType = ListType.ONS;

    private SearchResult expectedResult;

    @Mock
    private SearchClient searchClient;

    private FeaturedResultQuery featuredResultQuery;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        featuredResultQuery = new FeaturedResultQuery(searchTerm, listType);
        TestsUtil.setPrivateField(featuredResultQuery, "searchClient", searchClient);

        MockedFeaturedResultResponse contentResponse = new MockedFeaturedResultResponse();
        expectedResult = contentResponse.getSearchResult();

        MockedHttpRequest mockedHttpRequest = new MockedHttpRequest(featuredResultQuery.targetUri().build(), contentResponse);

        when(searchClient.get(featuredResultQuery.targetUri().toString()))
                .thenReturn(mockedHttpRequest);

        when(searchClient.post(featuredResultQuery.targetUri().toString()))
                .thenReturn(mockedHttpRequest);
    }

    @Test
    public void testSearchResult() throws Exception {
        SearchResult actual = featuredResultQuery.call();
        String actualJson = MAPPER.writeValueAsString(actual);
        String expectedJson = MAPPER.writeValueAsString(expectedResult);

        assertEquals(actualJson, expectedJson);
    }

}
