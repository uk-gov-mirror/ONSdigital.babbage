package com.github.onsdigital.template;

import com.github.onsdigital.content.page.base.PageType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bren on 08/06/15.
 */
public class TemplateMapping {

    private final static Map<PageType, String> templateMapping = new HashMap<>();

    static {
        put(PageType.home_page, "t1");
        put(PageType.taxonomy_landing_page, "t2");
        put(PageType.product_page, "t3");
        put(PageType.bulletin, "t4-1");
        put(PageType.article, "t4-2");
        put(PageType.timeseries, "t5-1");
        put(PageType.data_slice, "t5-2");
        put(PageType.compendium_landing_page, "t6-1");
        put(PageType.compendium, "t6-2");
    }


    private static void put(PageType type , String templateName) {
        templateMapping.put(type, templateName);
    }

    public static String getTemplateName(PageType pageType) {
        return templateMapping.get(pageType);
    }

}

