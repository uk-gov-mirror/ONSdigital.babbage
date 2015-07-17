package com.github.onsdigital.template;

import java.io.IOException;
import java.util.Map;

/**
 * Created by bren on 28/05/15.
 */
public interface TemplateRenderer {

    String renderTemplate(String templateName, Object data) throws IOException;

    String renderTemplate(String templateName, Object data, Map<String, ?> additionalData) throws IOException;
}
