//package com.github.onsdigital.babbage.request.handler.base;
//
//import com.github.onsdigital.babbage.content.client.ContentReadException;
//import com.github.onsdigital.babbage.paginator.Paginator;
//import com.github.onsdigital.babbage.response.base.BabbageResponse;
//import com.github.onsdigital.babbage.response.BabbageStringResponse;
//import com.github.onsdigital.babbage.search.AggregateQuery;
//import com.github.onsdigital.babbage.search.ONSQuery;
//import com.github.onsdigital.babbage.search.SearchService;
//import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
//import com.github.onsdigital.babbage.search.helpers.SearchRequestQueryBuilder;
//import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
//import com.github.onsdigital.babbage.search.input.SortBy;
//import com.github.onsdigital.babbage.search.model.ContentType;
//import com.github.onsdigital.babbage.search.model.field.FilterableField;
//import com.github.onsdigital.babbage.template.TemplateService;
//import com.github.onsdigital.babbage.util.URIUtil;
//import com.github.onsdigital.babbage.util.json.JsonUtil;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.util.*;
//
//import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.addTermAggregation;
//import static com.github.onsdigital.babbage.util.URIUtil.cleanUri;
//import static javax.ws.rs.core.MediaType.TEXT_HTML;
//
///**
// * Render a list page for bulletins under the given URI.
// */
//public abstract class ListPageBaseRequestHandler {
//
//    /**
//     * The type of page to be returned in the list page
//     *
//     * @return
//     */
//    protected abstract ContentType[] getAllowedTypes();
//
//    /**
//     * Used as a flag to decide if listed contents should be filtered with latestFlag=true
//     *
//     * @param request
//     * @return
//     */
//    protected boolean isFilterLatest(HttpServletRequest request) {
//        return false;
//    }
//
//    protected boolean isListTopics() {
//        return isLocalisedUri() == false;
//    }
//
//    protected boolean isAggregateByType() {
//        return false;
//    }
//
//    /**
//     * List page request handler is registered with this request type, requests made ending with request type is delegated to the handler automatically
//     * <p/>
//     * e.g.   if request type is publications any http request urls ending in /publications will be delegated to publications handler
//     *
//     * @return
//     */
//    public abstract String getRequestType();
//
//    /**
//     * Return true if the list page is localised to a uri.
//     * e.g the bulletin page is localised to t3 level uri, whereas the FOI
//     * list page is not localised and contains all FOI's.
//     *
//     * @return
//     */
//    public abstract boolean isLocalisedUri();
//
//
//    public BabbageResponse getData(String requestedUri, HttpServletRequest request) throws Exception {
//        System.out.println("List page data request from " + this.getClass().getSimpleName() + " for uri: " + requestedUri);
//        LinkedHashMap<String, Object> listData = prepareData(requestedUri, request);
//        return new BabbageStringResponse(JsonUtil.toJson(listData));
//    }
//
//    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
//        System.out.println("List page request from " + this.getClass().getSimpleName() + " for uri: " + requestedUri);
//        String html = TemplateService.getInstance().renderContent(prepareData(requestedUri, request));
//        return new BabbageStringResponse(html, TEXT_HTML);
//    }
//
//    protected LinkedHashMap<String, Object> prepareData(String requestedUri, HttpServletRequest request) throws IOException, ContentReadException {
//        ONSQuery query = createQuery(requestedUri, request);
//        AggregateQuery aggregateQuery = buildAggregateQuery(query);
//        List<SearchResponseHelper> responseHelpers = aggregateQuery == null ? doSearch(request, query) : doSearch(request,query,aggregateQuery);
//        Iterator<SearchResponseHelper> iterator = responseHelpers.iterator();
//        SearchResponseHelper results = iterator.next();
//        SearchResponseHelper aggregateResults = null;
//        if (iterator.hasNext()) {
//             aggregateResults = iterator.next();
//        }
//        Paginator.assertPage(query.getPage(), results);
//        return resolveListData(request, query, results, aggregateResults);
//    }
//
//    protected AggregateQuery buildAggregateQuery(ONSQuery query) {
//        if (isAggregateByType()) {
//            AggregateQuery aggregateQuery = new AggregateQuery();
//            //Applies aggregations on all types, regardless of filters as counts for all types are needed
//            aggregateQuery.setTypes(ContentType.getNamesOf(getAggregationTypes()))
//                    .setFields(query.getFields())
//                    .setSearchTerm(query.getSearchTerm())
//                    .setFilters(query.getFilters());
//            addTermAggregation(aggregateQuery, "count_by_type", FilterableField._type);
//            return aggregateQuery;
//        }
//        return null;
//    }
//
//    //Types to count documents for, by default it is all document types, override for different behaviour
//    protected ContentType[] getAggregationTypes() {
//        return getAllowedTypes();
//    }
//
//    protected LinkedHashMap<String, Object> resolveListData(HttpServletRequest request, ONSQuery query, SearchResponseHelper responseHelper, SearchResponseHelper aggregateResponseHelper) throws IOException {
//        LinkedHashMap<String, Object> listData = new LinkedHashMap<>();
//        listData.put("result", responseHelper.getResult());
//        listData.put("paginator", Paginator.getPaginator(query.getPage(), responseHelper));
//        listData.putAll(getBaseData(request));
//        if (aggregateResponseHelper != null) {
//            listData.put("counts", aggregateResponseHelper.getResult());
//        }
//        return listData;
//    }
//
//    /**
//     * Base data to render the correct template, can be used for rendering empty page with no results
//     *
//     * @return
//     * @throws IOException
//     */
//    protected LinkedHashMap<String, Object> getBaseData(HttpServletRequest request) throws IOException {
//        LinkedHashMap<String, Object> listData = new LinkedHashMap<>();
//        listData.put("type", "list");
//        listData.put("listType", getRequestType());
//        listData.put("uri", URIUtil.cleanUri(request.getRequestURI()));//full uri in the context to resolve breadcrumb
//        if (isListTopics()) {
//            listData.put("topics", getTopics());
//        }
//        return listData;
//    }
//
//    protected ONSQuery createQuery(String requestedUri, HttpServletRequest request) throws IOException, ContentReadException {
//        String uri = processUri(requestedUri, request);
//        ONSQuery query = new SearchRequestQueryBuilder(request, uri, getAllowedTypes()).buildQuery();
//        if (isFilterLatest(request)) {
//            SearchRequestHelper.addTermFilter(query, FilterableField.latestRelease, true);
//        }
//        return query;
//    }
//
//    private String processUri(String requestedUri, HttpServletRequest request) {
//        String uri = isLocalisedUri() ? requestedUri : cleanUri(request.getParameter("topic"));
//        uri = uri.endsWith("/") ? uri : (uri + "/");
//        return uri;
//    }
//
//    protected List<SearchResponseHelper> doSearch(HttpServletRequest request, ONSQuery... queries) throws IOException, ContentReadException {
//        if (queries.length > 1) {
//            return SearchService.getInstance().searchMultiple(queries);
//        } else {
//            ArrayList<SearchResponseHelper> responses = new ArrayList<>();
//            responses.add(SearchService.getInstance().search(queries[0]));
//            return responses;
//        }
//    }
//
//    private List<Topic> getTopics() throws IOException {
//        ONSQuery topicListQuery = new ONSQuery(ContentType.product_page.name()).setSize(Integer.MAX_VALUE);
//        SearchRequestHelper.addSort(topicListQuery, SortBy.TITLE);
//        SearchResponseHelper search = SearchService.getInstance().search(topicListQuery);
//        List<Map<String, Object>> results = search.getResult().getResults();
//        List<Topic> topics = new ArrayList<>();
//
//        for (Map<String, Object> result : results) {
//            String uri = (String) result.get("uri");
//            String title = null;
//            Map<String, Object> description = (Map<String, Object>) result.get("description");
//            if (description != null) {
//                title = (String) description.get("title");
//            }
//            topics.add(new Topic(uri, title));
//        }
//        return topics;
//    }
//
//    public class Topic {
//        private String uri;
//        private String title;
//
//        public Topic(String uri, String title) {
//            this.uri = uri;
//            this.title = title;
//        }
//
//        public String getUri() {
//            return uri;
//        }
//
//        public void setUri(String uri) {
//            this.uri = uri;
//        }
//
//        public String getTitle() {
//            return title;
//        }
//
//        public void setTitle(String title) {
//            this.title = title;
//        }
//    }
//
//
//}
