package com.github.onsdigital.babbage.search.external.requests.search.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.search.external.MockedHttpRequest;
import com.github.onsdigital.babbage.search.external.SearchClient;
import com.github.onsdigital.babbage.search.external.TestSearchResponseUtils;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
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
        MockitoAnnotations.initMocks(this);

        contentQuery = new ContentQuery(searchTerm, listType, page, pageSize);
        TestsUtil.setPrivateField(contentQuery, "searchClient", searchClient);

        expectedResult = TestSearchResponseUtils.testSearchResult();

        when(searchClient.get(contentQuery.targetUri().toString()))
                .thenReturn(new MockedHttpRequest(contentQuery.targetUri().build()));

        when(searchClient.post(contentQuery.targetUri().toString()))
                .thenReturn(new MockedHttpRequest(contentQuery.targetUri().build()));
    }

    @Test
    public void testSearchResult() throws Exception {
        SearchResult actual = contentQuery.call();
        String actualJson = MAPPER.writeValueAsString(actual);
        String expectedJson = MAPPER.writeValueAsString(expectedResult);

        assertEquals(actualJson, expectedJson);
    }

}
