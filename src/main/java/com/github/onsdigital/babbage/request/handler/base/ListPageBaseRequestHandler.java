package com.github.onsdigital.babbage.request.handler.base;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.request.response.BabbageResponse;
import com.github.onsdigital.babbage.request.response.BabbageStringResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.content.util.URIUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Map;

import static com.github.onsdigital.babbage.util.RequestUtil.getQueryParameters;

/**
 * Render a list page for bulletins under the given URI.
 */
public abstract class ListPageBaseRequestHandler implements RequestHandler {

    private final static String DATA_REQUEST = "data";
    public static final String CONTENT_TYPE = "text/html";

    /**
     * The type of page to be returned in the list page
     * @return
     */
    public abstract String[] getListTypes();

    /**
     * The template to use when rendering the page.
     * @return
     */
    public abstract String getTemplateName();

    /**
     * Return true if the list page is localised to a uri.
     * e.g the bulletin page is localised to t3 level uri, whereas the FOI
     * list page is not localised and contains all FOI's.
     * @return
     */
    public abstract boolean useLocalisedUri();

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {

        BabbageResponse babbageResponse;
        String type = URIUtil.resolveRequestType(request.getRequestURI());

        // trim /data from the uri if its a data request.
        if (type.equals(DATA_REQUEST)) {
            requestedUri = URIUtil.removeEndpoint(requestedUri);
        }

        String uri = "";
        if (useLocalisedUri()) {
            uri = requestedUri;
        }

        Map<String, String[]> queryParameters = getQueryParameters(request);
        queryParameters.put("type", getListTypes());

        try (InputStream dataStream = ContentClient.getInstance().getList(uri, queryParameters).getDataStream()) {
            String html = TemplateService.getInstance().renderTemplate(getTemplateName(), dataStream);
            babbageResponse = new BabbageStringResponse(html, CONTENT_TYPE);
        }

//        switch (type) {
//            case DATA_REQUEST:
//                babbageResponse = new BabbageStringResponse(IOUtils.toString(zebedeeResponse));
//                break;
//            default:
//                SearchResultsPage searchResultsPage = ContentUtil.deserialise(zebedeeResponse, SearchResultsPage.class);
//                searchResultsPage.setNavigation(NavigationUtil.getNavigation());
//                String html = TemplateService.getInstance().render(searchResultsPage, getTemplateName());
//
//                babbageResponse = new BabbageStringResponse(html, CONTENT_TYPE);
//                break;
//        }

        return babbageResponse;
    }
}
