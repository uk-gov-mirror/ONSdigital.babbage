package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.request.response.BabbageResponse;
import com.github.onsdigital.babbage.request.response.BabbageStringResponse;
import com.github.onsdigital.babbage.search.ONSQueryBuilder;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.query.SortOrder;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.URIUtil;
import com.github.onsdigital.babbage.util.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.LinkedHashMap;

import static com.github.onsdigital.babbage.util.json.JsonUtil.toJson;

/**
 * Render a list page for the given URI.
 */
public class PreviousReleasesRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "previousreleases";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        LinkedHashMap<String, Object> uri = new LinkedHashMap<>();
        uri.put("uri", requestedUri);
        ONSQueryBuilder queryBuilder = new ONSQueryBuilder()
                .setUriPrefix(URIUtil.removeLastSegment(requestedUri))
                .addSort("releaseDate", SortOrder.DESC);
        SearchResponseHelper searchResponseHelper = SearchService.getInstance().search(queryBuilder);
        String json =searchResponseHelper.toJson();
        String html =  TemplateService.getInstance().renderTemplate("content/t9-6", toJson(uri), json );
        return new BabbageStringResponse(html, MediaType.TEXT_HTML);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
