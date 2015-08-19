package com.github.onsdigital.request.handler.base;

import com.github.onsdigital.content.util.URIUtil;
import com.github.onsdigital.data.zebedee.ZebedeeClient;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

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
    public abstract String getListType();

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
        return get(requestedUri, request, null);
    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {

        BabbageResponse babbageResponse;
        String type = URIUtil.resolveRequestType(request.getRequestURI());

        // trim /data from the uri if its a data request.
        if (type.equals(DATA_REQUEST)) {
            requestedUri = URIUtil.removeEndpoint(requestedUri);
        }

        URIBuilder uriBuilder = new URIBuilder("list")
                .addParameter("type", getListType());

        if (useLocalisedUri()) {
            uriBuilder.addParameter("uri", requestedUri);
        }

        ZebedeeClient zebedeeClient = new ZebedeeClient(zebedeeRequest);
        InputStream zebedeeResponse =  zebedeeClient.get(uriBuilder.build().toString(), request.getRequestURI(), false);

        babbageResponse = new BabbageStringResponse(IOUtils.toString(zebedeeResponse));

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
