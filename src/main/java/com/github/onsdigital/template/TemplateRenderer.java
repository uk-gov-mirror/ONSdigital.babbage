package com.github.onsdigital.template;

import com.github.onsdigital.content.page.base.Page;

import java.io.IOException;

/**
 * Created by bren on 28/05/15.
 */
public interface TemplateRenderer {

    public String renderTemplate(String templateName, Page page) throws IOException;

}
