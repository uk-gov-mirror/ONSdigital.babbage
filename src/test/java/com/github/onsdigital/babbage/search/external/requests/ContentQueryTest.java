package com.github.onsdigital.babbage.search.external.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.search.external.SearchClient;
import com.github.onsdigital.babbage.search.external.requests.search.ContentQuery;
import com.github.onsdigital.babbage.search.external.requests.search.ListType;
import com.github.onsdigital.babbage.search.external.requests.mocks.response.MockContentResponse;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
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

    private String expectedResult;

    @Mock
    private SearchClient searchClient;

    private ContentQuery contentQuery;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        contentQuery = new ContentQuery(searchTerm, listType, page, pageSize);
        TestsUtil.setPrivateField(contentQuery, "searchClient", searchClient);

        CloseableHttpResponse response = new MockContentResponse();
        expectedResult = EntityUtils.toString(response.getEntity());

        when(searchClient.execute(contentQuery))
                .thenReturn(response);
    }

    @Test
    public void testSearchResult() throws Exception {
        SearchResult actual = contentQuery.call();
        String actualJson = MAPPER.writeValueAsString(actual);

        assertEquals(actualJson, expectedResult);
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
