package com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util;

import ch.qos.logback.classic.Level;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.logging.Log;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.http.ClientConfiguration;
import com.github.onsdigital.babbage.util.http.PooledHttpClient;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines the format of the custom markdown tags for maps and defines how to replace them.
 */
public class MapTagReplacer extends TagReplacementStrategy {

    private static final Pattern pattern = Pattern.compile("<ons-map\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");
    private static final String RENDERER_HOST = Configuration.MAP_RENDERER.getHost();
    private static final String RENDERER_PATH = Configuration.MAP_RENDERER.getHtmlPath();
    static final PooledHttpClient HTTP_CLIENT = new PooledHttpClient(RENDERER_HOST, createHttpConfiguration());

    private final String template;
    private final ContentClient contentClient;
    private final TemplateService templateService;
    private final PooledHttpClient httpClient;

    public MapTagReplacer(String path, String template) {
        this(path, template, ContentClient.getInstance(), TemplateService.getInstance(), HTTP_CLIENT);
    }

    public MapTagReplacer(String path, String template, ContentClient contentClient, TemplateService templateService, PooledHttpClient httpClient) {
        super(path);
        this.template = template;
        this.contentClient = contentClient;
        this.templateService = templateService;
        this.httpClient = httpClient;
    }

    /**
     * Gets the pattern that this strategy is applied to.
     *
     * @return
     */
    @Override
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * The function that generates the replacement text for each match.
     *
     * @param matcher
     * @return
     * @throws IOException
     */
    @Override
    public String replace(Matcher matcher) throws IOException {

        String tagPath = matcher.group(1);
        String figureUri = resolveFigureUri(this.getPath(), Paths.get(tagPath)) + ".json";

        try {
            ContentResponse contentResponse = contentClient.getResource(figureUri);
            String mapJson = contentResponse.getAsString();
            String html = invokeMapRenderer(mapJson);
            Map<String, Object> context = JsonUtil.toMap(mapJson);
            String result = templateService.renderTemplate(template, context, Collections.singletonMap("mapHtml", html));
            return result;
        } catch (ResourceNotFoundException e) {
            Log.buildDebug("Failed to find figure data for map.").addParameter("URL", figureUri).log();
            return templateService.renderTemplate(figureNotFoundTemplate);
        } catch (ContentReadException | MapRendererException e) {
            Log.build("Failed rendering map, uri: " + figureUri + ", error: " + e, Level.ERROR).log();
            return matcher.group();
        }
    }

    private String invokeMapRenderer(String postBody) throws MapRendererException {
        try (CloseableHttpResponse response = httpClient.sendPost(RENDERER_PATH, null, postBody)){
            return IOUtils.toString(response.getEntity().getContent());
        } catch (IOException e) {
            throw new MapRendererException(e);
        }
    }

    private static ClientConfiguration createHttpConfiguration() {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxTotalConnection(Configuration.MAP_RENDERER.getMaxServerConnection());
        configuration.setDisableRedirectHandling(true);
        return configuration;
    }

    /** MapRendererException is thrown and caught internally in this class only. */
    private static class MapRendererException extends Exception {
        public MapRendererException(Throwable cause) {
            super(cause);
        }
    }
}
