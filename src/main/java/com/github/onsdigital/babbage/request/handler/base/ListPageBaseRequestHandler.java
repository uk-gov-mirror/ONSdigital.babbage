package com.github.onsdigital.babbage.request.handler.base;

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

        LinkedHashMap<String, Object> listData = new LinkedHashMap<>();
        listData.put("uri", request.getRequestURI());//set full uri in the context
        listData.put("type", type);
        String html = TemplateService.getInstance().renderListPage(type, list(uri, request), JsonUtil.toJson(listData));
        babbageResponse = new BabbageStringResponse(html, CONTENT_TYPE);
        return babbageResponse;
    }

    protected String list(String uri, HttpServletRequest request) throws IOException {
        SearchResponseHelper helper = new ListSearchHelper().list(uri, getTypeSet(), request);
        return helper.toJson();
    }

    private Set<String> getTypeSet() {
        return new HashSet<>(Arrays.asList(getAllowedTypes()));
    }
}
