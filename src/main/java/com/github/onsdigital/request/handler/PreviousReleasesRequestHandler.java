package com.github.onsdigital.request.handler;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.content.DirectoryListing;
import com.github.onsdigital.content.link.PageReference;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.list.ListPage;
import com.github.onsdigital.content.partial.SearchResult;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;
import com.github.onsdigital.template.TemplateService;
import com.github.onsdigital.util.NavigationUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Render a list page for the given URI.
 */
public class PreviousReleasesRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "previousreleases";

    public static final String CONTENT_TYPE = "text/html";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return get(requestedUri, request, null);
    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {

        // read the previous releases by looking at the file system. To be replaced with a search engine query.
        ListPage page = new ListPage();
        List<PageReference> pageReferences = new ArrayList<>();
        DataService dataService = DataService.getInstance();

        DirectoryListing listing = dataService.readDirectory(requestedUri, zebedeeRequest);
        List<String> folders = new ArrayList<>(listing.folders.keySet());
        Collections.sort(folders, Collections.reverseOrder());

        for (String folder : folders) {
            URI pageReferenceUri = URI.create(requestedUri + "/" + folder);
            PageReference pageReference = new PageReference(pageReferenceUri);
            Page referencedPage = dataService.readAsPage(pageReferenceUri.toString(), false, zebedeeRequest);
            pageReference.setDescription(referencedPage.getDescription());
            pageReferences.add(pageReference);
        }

        // build the search result page dynamically instead of from a json file.
        SearchResult result = new SearchResult();
        result.setResults(pageReferences);
        result.setNumberOfResults(pageReferences.size());
        page.setContentSearchResult(result);

        //TODO: Read navigaton from zebedee if zebedee request ????
        page.setNavigation(NavigationUtil.getNavigation());
        String html = TemplateService.getInstance().renderPage(page);
        return new BabbageStringResponse(html, CONTENT_TYPE);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
