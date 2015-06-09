package com.github.onsdigital.template;

import com.github.onsdigital.content.base.ContentType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bren on 08/06/15.
 */
public class TemplateMapping {

    private final static Map<ContentType, String> templateMapping = new HashMap<>();

    static {
        put(ContentType.home_page, "t1");
        put(ContentType.taxonomy_landing_page, "t2");
        put(ContentType.product_page, "t3");
        put(ContentType.bulletin, "t4-1");
        put(ContentType.article, "t4-2");
        put(ContentType.timeseries, "t5-1");
        put(ContentType.data_slice, "t5-2");
        put(ContentType.compendium_landing_page, "t6-1");
        put(ContentType.compendium, "t6-2");
    }


    private static void put(ContentType type , String templateName) {
        templateMapping.put(type, templateName);
    }

    public static String getTemplateName(ContentType contentType) {
        return templateMapping.get(contentType);
    }

}

