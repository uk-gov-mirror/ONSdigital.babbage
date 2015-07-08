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
        put(PageType.compendium_chapter, "t6-2");
        put(PageType.compendium_data, "t6-3");
        put(PageType.reference_tables, "t8-1");
        put(PageType.dataset, "t8-3");
        put(PageType.static_landing_page, "t7-4-1");
        put(PageType.static_page, "t7-5");
        put(PageType.static_article, "t7-6");
        put(PageType.static_methodology, "t7-6");
        put(PageType.static_qmi, "t7-1");
        put(PageType.static_adhoc, "t7-2");
        put(PageType.static_foi, "t7-3");
        put(PageType.list_page, "t9-6");
        put(PageType.chart, "chart");
        put(PageType.table, "table");
        put(PageType.search_results_page, "t10");
        put(PageType.error404, "404");
        put(PageType.error500, "500");
        put(PageType.search_results_page, "t10");
    }


    private static void put(PageType type , String templateName) {
        templateMapping.put(type, templateName);
    }

    public static String getTemplateName(PageType pageType) {
        return templateMapping.get(pageType);
    }

}

