package com.github.onsdigital.babbage.search.external.requests.search.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.search.external.MockedContentResponse;
import com.github.onsdigital.babbage.search.external.MockedHttpRequest;
import com.github.onsdigital.babbage.search.external.SearchClient;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ContentQueryTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String searchTerm = "Who ya gonna call?";
    private final ListType listType = ListType.ONS;
    private final int page = 1;
    private final int pageSize = 10;

    private SearchResult expectedResult;

    @Mock
    private SearchClient searchClient;

    private ContentQuery contentQuery;

    @Before
    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//
//        contentQuery = new ContentQuery(searchTerm, listType, page, pageSize);
//        TestsUtil.setPrivateField(contentQuery, "searchClient", searchClient);
//
//        MockedContentResponse contentResponse = new MockedContentResponse();
//        expectedResult = contentResponse.getSearchResult();
//
//        MockedHttpRequest mockedHttpRequest = new MockedHttpRequest(contentQuery.targetUri().build(), contentResponse);
//
//        when(searchClient.get(contentQuery.targetUri()))
//                .thenReturn(mockedHttpRequest);
//
//        when(searchClient.post(contentQuery.targetUri()))
//                .thenReturn(mockedHttpRequest);
    }

    @Test
    public void testSearchResult() throws Exception {
        SearchResult actual = contentQuery.call();
        String actualJson = MAPPER.writeValueAsString(actual);
        String expectedJson = MAPPER.writeValueAsString(expectedResult);

        assertEquals(actualJson, expectedJson);
    }


    @Test
    public void testSpellCheckResult() throws Exception {
        SearchResult actual = contentQuery.call();

        // First suggestion should be 'rpi cpi'
        assertNotNull(actual.getSuggestions());
        assertTrue(actual.getSuggestions().size() > 0);

        String suggestion = actual.getSuggestions().get(0);
        assertEquals("rpi cpi", suggestion);
    }

}
