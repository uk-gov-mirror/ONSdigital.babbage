package com.github.onsdigital.template;

import java.io.IOException;
import java.util.Map;

/**
 * Created by bren on 28/05/15.
 */
public interface TemplateRenderer {

    /**
     * Renders the page based on type field in the data
     * @param data
     * @return
     * @throws IOException
     */
    String renderTemplate(Object data) throws IOException;

    /**
     * Renders the page based on type field in the data with additional data
     *
     * @param data
     * @param additionalData data will be accessible with given map key in the template. e.g. <"navigation", NavigationModel >
     * @return
     * @throws IOException
     */
    String renderTemplate(Object data, Map<String, Object> additionalData) throws IOException;

    /**
     * Renders given template with given data
     *
     * @param templateName
     * @param data
     * @return
     * @throws IOException
     */
    String renderTemplate(String templateName, Object data) throws IOException;

    String renderTemplate(String templateName, Object data, Map<String, Object> additionalData) throws IOException;
}
