package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.api.endpoint.rss.service.RssService;
import com.github.onsdigital.babbage.error.ValidationError;
import com.github.onsdigital.babbage.response.BabbageRssResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDates;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDatesException;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.github.onsdigital.babbage.util.TestsUtil.setPrivateStaticField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test the {@link ReleaseCalendar} api endpoint.
 */
public class ReleaseCalendarTest {

    static final String URI = "/this/is/a/uri";
    static final String CLASS_NAME = ReleaseCalendar.class.getSimpleName();

    @Mock
    private SearchService searchServiceMock;

    @Mock
    private RssService rssServiceMock;

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private PublishDates publishDatesMock;

    @Mock
    private BabbageResponse babbageResponseMock;

    @Mock
    private BabbageRssResponse babbageRssResponseMock;

    private ReleaseCalendar releaseCalendar;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        releaseCalendar = new ReleaseCalendar();

        setPrivateStaticField(releaseCalendar, "searchService", searchServiceMock);
        setPrivateStaticField(releaseCalendar, "rssService", rssServiceMock);
    }

    @Test
    public void shouldReturnPageWithoutValidationErrors() throws Exception {
        when(rssServiceMock.isRssRequest(requestMock))
                .thenReturn(false);

        when(searchServiceMock.extractPublishDates(requestMock))
                .thenReturn(publishDatesMock);

        when(searchServiceMock.listPage(eq(CLASS_NAME), any(SearchQueries.class)))
                .thenReturn(babbageResponseMock);

        BabbageResponse response = releaseCalendar.get(URI, requestMock);

        assertThat("Incorrect response returned", response, equalTo(babbageResponseMock));
        verify(rssServiceMock, times(1)).isRssRequest(requestMock);
        verify(searchServiceMock, times(1)).extractPublishDates(requestMock);
        verify(searchServiceMock, times(1)).listPage(eq(CLASS_NAME), any(SearchQueries.class));
        verify(searchServiceMock, never()).listPageWithValidationErrors(eq(CLASS_NAME), any(SearchQueries.class),
                anyListOf(ValidationError.class));
        verifyNoMoreInteractions(rssServiceMock);
    }

    @Test
    public void shouldReturnPageWithValidationErrorsForInvalidDate() throws Exception {
        when(rssServiceMock.isRssRequest(requestMock))
                .thenReturn(false);

        List<ValidationError> errorList = new ImmutableList.Builder<ValidationError>()
                .add(PublishDates.PUBLISHED_FROM_INVALID).build();

        PublishDatesException expectedError = new PublishDatesException(errorList);

        when(searchServiceMock.extractPublishDates(requestMock))
                .thenThrow(expectedError);

        when(searchServiceMock.listPageWithValidationErrors(eq(CLASS_NAME), any(SearchQueries.class), eq(errorList)))
                .thenReturn(babbageResponseMock);

        BabbageResponse response = releaseCalendar.get(URI, requestMock);

        assertThat("Incorrect response returned", response, equalTo(babbageResponseMock));
        verify(rssServiceMock, times(1)).isRssRequest(requestMock);
        verify(searchServiceMock, times(1)).extractPublishDates(requestMock);
        verify(searchServiceMock, never()).listPage(eq(CLASS_NAME), any(SearchQueries.class));
        verify(searchServiceMock, times(1)).listPageWithValidationErrors(eq(CLASS_NAME), any(SearchQueries.class), eq(errorList));
        verifyNoMoreInteractions(rssServiceMock);
    }

    @Test
    public void shouldReturnRSSResponseForRSSRequests() throws Exception {
        when(rssServiceMock.isRssRequest(requestMock))
                .thenReturn(true);

        when(rssServiceMock.getReleaseCalendarFeedResponse(eq(requestMock), any(SearchQueries.class)))
                .thenReturn(babbageRssResponseMock);

        BabbageResponse babbageResponse = releaseCalendar.get(URI, requestMock);

        assertThat("Incorrect response returned", babbageResponse, equalTo(babbageRssResponseMock));
        verify(rssServiceMock, times(1)).isRssRequest(requestMock);
        verify(rssServiceMock, times(1)).getReleaseCalendarFeedResponse(eq(requestMock), any(SearchQueries.class));
        verifyZeroInteractions(searchServiceMock);
    }

    @Test
    public void shouldReturnPageAsJson() throws Exception {
        when(searchServiceMock.listJson(eq(CLASS_NAME), any(SearchQueries.class)))
                .thenReturn(babbageResponseMock);

        BabbageResponse response = releaseCalendar.getData(URI, requestMock);
        assertThat("Incorrect response returned", response, equalTo(babbageResponseMock));
        verify(searchServiceMock, times(1)).listJson(eq(CLASS_NAME), any(SearchQueries.class));
    }
}
