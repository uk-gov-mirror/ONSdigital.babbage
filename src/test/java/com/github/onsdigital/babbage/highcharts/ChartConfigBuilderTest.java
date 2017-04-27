package com.github.onsdigital.babbage.highcharts;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.NOTES_PARAM;
import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.SHOW_NOTES_PARAM;
import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.SHOW_SOURCE_PARAM;
import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.SHOW_SUBTITLE_PARAM;
import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.SHOW_TITLE_PARAM;
import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.SOURCE_PARAM;
import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.SUBTITLE_PARAM;
import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.TITLE_PARAM;
import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.WIDTH_PARAM;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test to verify the {@link ChartConfigBuilder} behaves as expected for valid & invalid scenarios/
 */
public class ChartConfigBuilderTest {

    @Mock
    private HttpServletRequest requestMock;

    private ChartConfigBuilder builder;
    private Map<String, Object> expected;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        builder = new ChartConfigBuilder();
        expected = new HashMap<>();
    }

    @Test
    public void shouldCreateExpetedConfig() throws Exception {
        when(requestMock.getParameter(TITLE_PARAM))
                .thenReturn("true");

        when(requestMock.getParameter(SUBTITLE_PARAM))
                .thenReturn("");

        when(requestMock.getParameter(SOURCE_PARAM))
                .thenReturn("FALSE");

        when(requestMock.getParameter(NOTES_PARAM))
                .thenReturn("ABCDEFG");

        expected.put(SHOW_TITLE_PARAM, true);
        expected.put(SHOW_SUBTITLE_PARAM, false);
        expected.put(SHOW_SOURCE_PARAM, false);
        expected.put(SHOW_NOTES_PARAM, false);
        expected.put(WIDTH_PARAM, 500);

        builder.showTitle(requestMock)
                .showSubTitle(requestMock)
                .showSource(requestMock)
                .showNotes(requestMock)
                .width(500);

        assertThat(expected, equalTo(builder.getMap()));
        verify(requestMock, times(1)).getParameter(TITLE_PARAM);
        verify(requestMock, times(1)).getParameter(SUBTITLE_PARAM);
        verify(requestMock, times(1)).getParameter(SOURCE_PARAM);
        verify(requestMock, times(1)).getParameter(NOTES_PARAM);
    }
}
