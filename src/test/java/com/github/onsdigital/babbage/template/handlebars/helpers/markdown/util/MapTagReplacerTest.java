package com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.http.PooledHttpClient;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.regex.Matcher;

import static com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util.MapTagReplacer.MapType.PNG;
import static com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util.MapTagReplacer.MapType.SVG;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MapTagReplacerTest {

    private final String mapHtml = "<map></map>";
    private final String path = "/myPath/";
    private final String template = "myTemplate";
    private final String markdownContent = "<ons-map path=\"mapid\" />";
    private final String renderedTemplate = "renderedTemplate";
    @Mock
    private ContentClient contentClientMock;
    @Mock
    private TemplateService templateServiceMock;
    @Mock
    private PooledHttpClient httpClientMock;
    @Mock
    private ContentResponse contentResponseMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CloseableHttpResponse responseMock;
    @Mock
    private ContentReadException readException;

    private Matcher matcher;
    private MapTagReplacer testObj;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        testObj = new MapTagReplacer(path, template, contentClientMock, templateServiceMock, httpClientMock, SVG);

        when(contentClientMock.getResource(path + "mapid.json")).thenReturn(contentResponseMock);

        matcher = testObj.getPattern().matcher(markdownContent);
        matcher.find();
    }

    @Test
    public void replaceShouldGetContentAndInvokeMapRendererForSvg() throws Exception {
        String json = "{\"foo\": \"bar\"}";
        when(contentResponseMock.getAsString()).thenReturn(json);
        when(httpClientMock.sendPost(Configuration.MAP_RENDERER.getSvgPath(), singletonMap("Content-Type", "application/json;charset=utf-8"), json, "UTF-8")).thenReturn(responseMock);
        when(responseMock.getEntity().getContent()).thenReturn(IOUtils.toInputStream(mapHtml));
        when(templateServiceMock.renderTemplate(template, singletonMap("foo", "bar"), singletonMap("mapHtml", mapHtml))).thenReturn(renderedTemplate);

        String result = testObj.replace(matcher);

        assertThat(result, equalTo(renderedTemplate));
    }

    @Test
    public void replaceShouldGetContentAndInvokeMapRendererForPng() throws Exception {
        testObj = new MapTagReplacer(path, template, contentClientMock, templateServiceMock, httpClientMock, PNG);
        String json = "{\"foo\": \"bar\"}";
        when(contentResponseMock.getAsString()).thenReturn(json);
        when(httpClientMock.sendPost(Configuration.MAP_RENDERER.getPngPath(), singletonMap("Content-Type", "application/json;charset=utf-8"), json, "UTF-8")).thenReturn(responseMock);
        when(responseMock.getEntity().getContent()).thenReturn(IOUtils.toInputStream(mapHtml));
        when(templateServiceMock.renderTemplate(template, singletonMap("foo", "bar"), singletonMap("mapHtml", mapHtml))).thenReturn(renderedTemplate);

        String result = testObj.replace(matcher);

        assertThat(result, equalTo(renderedTemplate));
    }

    @Test
    public void replaceShouldRenderFigureNotFoundTemplateIfResourceNotFound() throws Exception {
        when(contentClientMock.getResource(anyString())).thenThrow(new ResourceNotFoundException());
        when(templateServiceMock.renderTemplate(TagReplacementStrategy.figureNotFoundTemplate)).thenReturn(TagReplacementStrategy.figureNotFoundTemplate);

        String result = testObj.replace(matcher);

        assertThat(result, equalTo(TagReplacementStrategy.figureNotFoundTemplate));
    }

    @Test
    public void replaceShouldRenderOriginalContentWhenContentNotFound() throws Exception {
        when(contentClientMock.getResource(anyString())).thenThrow(readException);

        String result = testObj.replace(matcher);

        assertThat(result, equalTo(markdownContent));
    }
}