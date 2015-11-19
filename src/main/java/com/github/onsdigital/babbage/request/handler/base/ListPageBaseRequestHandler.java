package com.github.onsdigital.babbage.request.handler.base;

import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.SearchRequestQueryBuilder;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.URIUtil;
import com.github.onsdigital.babbage.util.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    protected abstract ContentType[] getAllowedTypes();

    protected boolean isFilterLatest(HttpServletRequest request) {
        return false;
    }

    protected boolean isListTopics() {
        return isLocalisedUri() == false;
    }

    /**
     * Return true if the list page is localised to a uri.
     * e.g the bulletin page is localised to t3 level uri, whereas the FOI
     * list page is not localised and contains all FOI's.
     *
     * @return
     */
    public abstract boolean isLocalisedUri();


    public String getData(String requestedUri, HttpServletRequest request) throws Exception {

        System.out.println("List page data request from " + this.getClass().getSimpleName() + " for uri: " + requestedUri);
        ONSQuery query = createQuery(requestedUri, request);

        SearchResponseHelper responseHelper = doSearch(request, query);
        Paginator.assertPage(query.getPage(), responseHelper);
        return JsonUtil.toJson(responseHelper.getResult());
    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {

        System.out.println("List page request from " + this.getClass().getSimpleName() + " for uri: " + requestedUri);
        BabbageResponse babbageResponse;
        String type = URIUtil.resolveRequestType(request.getRequestURI());
        ONSQuery query = createQuery(requestedUri, request);
        SearchResponseHelper responseHelper = doSearch(request, query);
        Paginator.assertPage(query.getPage(), responseHelper);

        LinkedHashMap<String, Object> listData = new LinkedHashMap<>();
        listData.put("type", type);
        listData.put("paginator", Paginator.getPaginator(query.getPage(), responseHelper));
        listData.put("uri", request.getRequestURI());//set full uri in the context
        if (isListTopics()) {
            listData.put("topics", getTopics());
        }
        listData.put("result", responseHelper.getResult());
        String html = TemplateService.getInstance().renderContent(listData);
        babbageResponse = new BabbageStringResponse(html, CONTENT_TYPE);
        return babbageResponse;
    }

    protected ONSQuery createQuery(String requestedUri, HttpServletRequest request) {
        String uri = processUri(requestedUri, request);
        ONSQuery query = new SearchRequestQueryBuilder(request, uri, getAllowedTypes()).buildQuery();
        if (isFilterLatest(request)) {
            SearchRequestHelper.addTermFilter(query, FilterableField.latestRelease, true);
        }
        return query;
    }

    private String processUri(String requestedUri, HttpServletRequest request) {
        String uri = isLocalisedUri() ? requestedUri : cleanUri(request.getParameter("topic"));
        uri = uri.endsWith("/") ? uri : (uri + "/");
        return uri;
    }

    protected SearchResponseHelper doSearch(HttpServletRequest request, ONSQuery query) throws IOException {
        return SearchService.getInstance().search(query);
    }

    private List<Topic> getTopics() throws IOException {
        ONSQuery topicListQuery = new ONSQuery(ContentType.product_page.name()).setSize(Integer.MAX_VALUE);
        SearchRequestHelper.addSort(topicListQuery, SortBy.TITLE);
        SearchResponseHelper search = SearchService.getInstance().search(topicListQuery);
        List<Map<String, Object>> results = search.getResult().getResults();
        List<Topic> topics = new ArrayList<>();

        for (Map<String, Object> result : results) {
            String uri = (String) result.get("uri");
            String title = null;
            Map<String, Object> description = (Map<String, Object>) result.get("description");
            if (description != null) {
                title = (String) description.get("title");
            }
            topics.add(new Topic(uri, title));
        }
        return topics;
    }


    public class Topic {
        private String uri;
        private String title;

        public Topic(String uri, String title) {
            this.uri = uri;
            this.title = title;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }


}
