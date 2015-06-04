package com.github.onsdigital.template;

import com.github.onsdigital.json.DataItem;

import java.io.IOException;

/**
 * Created by bren on 28/05/15.
 */
public interface TemplateRenderer {

    public String renderTemplate(String templateName, String data) throws IOException;

}
