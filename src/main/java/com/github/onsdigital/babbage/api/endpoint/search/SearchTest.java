package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.search.ElasticSearchClient;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.Client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by bren on 11/01/16.
 */
@Api
public class SearchTest {

    private String templateName = "test/searchtest";
    private String[] types = {"bulletin", "article", "article_download", "compendium_landing_page", "timeseries", "dataset_landing_page", "reference_tables", "static_adhoc", "static_methodology", "static_methodology_download", "static_qmi", "static_article", "static_foi", "static_page", "static_landing_page"};

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String q = request.getParameter("q");
        if (StringUtils.isEmpty(q)) {
            renderTemplate(null, response);
        } else {
            String searchQuery = TemplateService.getInstance().renderTemplate("test/searchquery");
            searchQuery = searchQuery.replace("$q", q);
            Client client = ElasticSearchClient.getElasticsearchClient();
            String elasticSearchIndexAlias = Configuration.ELASTIC_SEARCH.getElasticSearchIndexAlias();
            SearchRequestBuilder contentSearchBuilder = client.prepareSearch(elasticSearchIndexAlias).setTypes(types)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setExtraSource(searchQuery);

            SearchRequestBuilder topicSearchBuilder = client.prepareSearch(elasticSearchIndexAlias)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setTypes("product_page");
            topicSearchBuilder.setSize(1).setExtraSource(searchQuery);

            MultiSearchRequestBuilder searchRequestBuilder = client.prepareMultiSearch();
            searchRequestBuilder.add(topicSearchBuilder);
            searchRequestBuilder.add(contentSearchBuilder);

            MultiSearchResponse searchResponses = searchRequestBuilder.get();

            SearchResponse topics = searchResponses.getResponses()[0].getResponse();
            SearchResponse contents = searchResponses.getResponses()[1].getResponse();
            SearchResponseHelper topicResponse = new SearchResponseHelper(topics);
            SearchResponseHelper contentResponse = new SearchResponseHelper(contents);

            Map<String, Object> templateData = new LinkedHashMap<>();
            templateData.put("contents", contentResponse.getResult());
            templateData.put("featured", topicResponse.getResult());

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.TEXT_HTML);
            renderTemplate(templateData, response);
        }
    }

    private void renderTemplate(Map<String, Object> data, HttpServletResponse response) throws IOException {
        IOUtils.write(TemplateService.getInstance().renderTemplate(templateName, JsonUtil.toJson(data)), response.getOutputStream());
    }

}
