package com.github.onsdigital.babbage.search.external.requests.search.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.search.external.MockedHttpRequest;
import com.github.onsdigital.babbage.search.external.MockedTypeCountsResponse;
import com.github.onsdigital.babbage.search.external.SearchClient;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class TypeCountsQueryTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String searchTerm = "Who ya gonna call?";
    private final ListType listType = ListType.ONS;

    private SearchResult expectedResult;

    @Mock
    private SearchClient searchClient;

    private TypeCountsQuery typeCountsQuery;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        typeCountsQuery = new TypeCountsQuery(searchTerm, listType);
        TestsUtil.setPrivateField(typeCountsQuery, "searchClient", searchClient);

        MockedTypeCountsResponse typeCountsResponse = new MockedTypeCountsResponse();
        expectedResult = typeCountsResponse.getSearchResult();

        MockedHttpRequest mockedHttpRequest = new MockedHttpRequest(typeCountsQuery.targetUri().build(), typeCountsResponse);

        when(searchClient.get(typeCountsQuery.targetUri().toString()))
                .thenReturn(mockedHttpRequest);

        when(searchClient.post(typeCountsQuery.targetUri().toString()))
                .thenReturn(mockedHttpRequest);
    }

    @Test
    public void testSearchResult() throws Exception {
        SearchResult actual = typeCountsQuery.call();
        String actualJson = MAPPER.writeValueAsString(actual);
        String expectedJson = MAPPER.writeValueAsString(expectedResult);

        assertEquals(actualJson, expectedJson);
    }

}
