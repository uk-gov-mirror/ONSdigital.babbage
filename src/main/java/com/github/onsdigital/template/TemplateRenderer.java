package com.github.onsdigital.template;

import com.github.onsdigital.content.base.Content;

import java.io.IOException;

/**
 * Created by bren on 28/05/15.
 */
public interface TemplateRenderer {

    public String renderTemplate(String templateName, Content data) throws IOException;

}
