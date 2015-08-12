package com.github.onsdigital.babbage.template;

import java.io.IOException;
import java.util.Map;

/**
 * Created by bren on 28/05/15.
 */
public interface TemplateRenderer {

    /**
     * Renders the page based on type field in the data with additional data
     *
     * @param data
     * @param additionalContext data will be accessible with given map key in the template. e.g. <"cookies", cookievalues>, can be null
     * @return
     * @throws IOException
     */
    String renderTemplate(Object data, Map<String, Object> additionalContext) throws IOException;

    /**
     * Renders given template with given data
     *
     * @param templateName
     * @param data
     * @param additionalContext data will be accessible with given map key in the template. e.g. <"cookies", cookievalues>, can be null
     * @return
     * @throws IOException
     */
    String renderTemplate(String templateName, Object data, Map<String, Object> additionalContext) throws IOException;
}
