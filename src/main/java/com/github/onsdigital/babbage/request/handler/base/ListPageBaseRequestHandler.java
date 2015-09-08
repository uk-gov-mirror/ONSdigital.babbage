package com.github.onsdigital.babbage.request.handler.base;

import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.request.handler.list.helper.ListSearchHelper;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import com.github.onsdigital.content.util.URIUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getMaxVisiblePaginatorLink;
import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getResultsPerPage;
import static com.github.onsdigital.babbage.util.SearchRequestUtil.extractPage;

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
        }

        int page = extractPage(request);
        SearchResponseHelper responseHelper = list(uri, page, request);

        LinkedHashMap<String, Object> listData = new LinkedHashMap<>();
        setPaginator(page, responseHelper, listData);
        listData.put("uri", request.getRequestURI());//set full uri in the context
        listData.put("type", type);

        String html = TemplateService.getInstance().renderListPage(type, responseHelper.toJson(), JsonUtil.toJson(listData));
        babbageResponse = new BabbageStringResponse(html, CONTENT_TYPE);
        return babbageResponse;
    }


    protected boolean isPaginated() {
        return true;
    }

    protected SearchResponseHelper list(String uri, int page, HttpServletRequest request) throws IOException {
        SearchResponseHelper helper = new ListSearchHelper().list(uri, page, getTypeSet(), request);
        return helper;
    }

    private Set<String> getTypeSet() {
        return new HashSet<>(Arrays.asList(getAllowedTypes()));
    }


    private void setPaginator(int page, SearchResponseHelper responseHelper, LinkedHashMap<String, Object> listData) {
        if (isPaginated()) {
            if (page != 1 && responseHelper.getResult().getResults().size() == 0) {
                throw new ResourceNotFoundException("Non-existing page request");
            }
            Paginator paginator = new Paginator(responseHelper.getNumberOfResults(), getMaxVisiblePaginatorLink(), page, getResultsPerPage());
            if (paginator.getNumberOfPages() > 1) {
                listData.put("paginator", paginator);
            }
        }
    }


}
