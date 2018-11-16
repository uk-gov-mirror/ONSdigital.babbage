package com.github.onsdigital.babbage.search.external.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.search.external.SearchClient;
import com.github.onsdigital.babbage.search.external.requests.mocks.response.MockTypeCountsResponse;
import com.github.onsdigital.babbage.search.external.requests.search.TypeCountsQuery;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class TypeCountsQueryTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String searchTerm = "Who ya gonna call?";

    private String expectedResult;

    @Mock
    private SearchClient searchClient;

    private TypeCountsQuery typeCountsQuery;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        typeCountsQuery = new TypeCountsQuery(searchTerm);
        TestsUtil.setPrivateField(typeCountsQuery, "searchClient", searchClient);

        CloseableHttpResponse response = new MockTypeCountsResponse();
        expectedResult = EntityUtils.toString(response.getEntity());

        when(searchClient.execute(typeCountsQuery))
                .thenReturn(response);
    }

    @Test
    public void testSearchResult() throws Exception {
        SearchResult actual = typeCountsQuery.call();
        String actualJson = MAPPER.writeValueAsString(actual);

        assertEquals(actualJson, expectedResult);
    }

}
