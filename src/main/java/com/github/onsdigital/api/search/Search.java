package com.github.onsdigital.api.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.error.ErrorHandler;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.request.response.BabbageRedirectResponse;
import com.github.onsdigital.babbage.request.response.BabbageResponse;
import com.github.onsdigital.babbage.request.response.BabbageStringResponse;
import com.github.onsdigital.babbage.search.ONSQueryBuilder;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.content.util.URIUtil;
import com.github.onsdigital.error.ResourceNotFoundException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.onsdigital.babbage.util.RequestUtil.getQueryParameters;

@Api
public class Search {
    private final static String HTML_MIME = "text/html";
    private final static String DATA_REQUEST = "data";
    private final static String SEARCH_REQUEST = "search";

    @GET
    public Object get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, ContentNotFoundException, ContentReadException, URISyntaxException {

        BabbageResponse babbageResponse;
        String type = URIUtil.resolveRequestType(request.getRequestURI());

        switch (type) {
            case DATA_REQUEST:
                new BabbageStringResponse(search(request)).apply(response);
                break;
            case SEARCH_REQUEST:
                break;
            default:
                ErrorHandler.renderErrorPage(404, response);
        }
        return null;
    }


    private String search(HttpServletRequest request) throws IOException {
        SearchResponseHelper economy = SearchService.getInstance().search(new ONSQueryBuilder().setHighLightFields(true).setQuery("economy").setUriPrefix("/economy"));
        return economy.toJson();
    }
}