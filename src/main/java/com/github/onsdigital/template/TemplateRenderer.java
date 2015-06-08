package com.github.onsdigital.template;

import java.io.IOException;

/**
 * Created by bren on 28/05/15.
 */
public interface TemplateRenderer {

    public String renderTemplate(String templateName, Object data) throws IOException;

}
