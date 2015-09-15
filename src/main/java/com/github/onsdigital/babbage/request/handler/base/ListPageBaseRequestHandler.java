package com.github.onsdigital.babbage.request.handler.base;

import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import com.github.onsdigital.content.util.URIUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedHashMap;

import static com.github.onsdigital.babbage.util.URIUtil.cleanUri;

/**
 * Render a list page for bulletins under the given URI.
 */
public abstract class ListPageBaseRequestHandler implements RequestHandler {

    public static final String CONTENT_TYPE = "text/html";

    /**
     * The type of page to be returned in the list page
     *
     * @return
     */
    public abstract String[] getAllowedTypes();

    /**
     * Return true if the list page is localised to a uri.
     * e.g the bulletin page is localised to t3 level uri, whereas the FOI
     * list page is not localised and contains all FOI's.
     *
     * @return
     */
    public abstract boolean useLocalisedUri();

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {

        System.out.println("List page request from " + this.getClass().getSimpleName() + " for uri: " + requestedUri);

        BabbageResponse babbageResponse;
        String type = URIUtil.resolveRequestType(request.getRequestURI());
        String uri = "";
        if (useLocalisedUri()) {
            uri = requestedUri;
        } else {
            String topic = request.getParameter("topic");
            uri = cleanUri(topic);
        }

        SearchRequestHelper searchRequestHelper = new SearchRequestHelper(request, uri, getAllowedTypes());
        SearchResponseHelper responseHelper = doSearch(searchRequestHelper);

        Paginator.assertPage(searchRequestHelper.getPage(),responseHelper);

        LinkedHashMap<String, Object> listData = new LinkedHashMap<>();
        listData.put("paginator", Paginator.getPaginator(searchRequestHelper.getPage(), responseHelper));
        listData.put("uri", request.getRequestURI());//set full uri in the context
        listData.put("type", type);

        String html = TemplateService.getInstance().renderListPage(type, JsonUtil.toJson(responseHelper.getResult()), JsonUtil.toJson(listData));
        babbageResponse = new BabbageStringResponse(html, CONTENT_TYPE);
        return babbageResponse;
    }

    protected SearchResponseHelper doSearch(SearchRequestHelper searchRequestHelper) throws IOException {
        return SearchService.getInstance().search(searchRequestHelper.buildQuery());
    }


}
