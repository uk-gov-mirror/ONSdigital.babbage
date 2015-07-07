package com.github.onsdigital.request.handler;

import com.github.onsdigital.content.DirectoryListing;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.zebedee.ZebedeeClient;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;
import com.github.onsdigital.template.TemplateService;
import com.github.onsdigital.util.NavigationUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Render a list page for the given URI.
 */
public class LatestReleaseRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "latest";

    public static final String CONTENT_TYPE = "text/html";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return get(requestedUri, request, null);
    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {

        DirectoryListing listing;
        if (zebedeeRequest != null) {
            listing = readFromZebedee(requestedUri, zebedeeRequest);
        } else {
            listing = readFromLocal(requestedUri);
        }

        List<String> folders = new ArrayList<>(listing.folders.keySet());
        Collections.sort(folders, Collections.reverseOrder());
        String release = new File(folders.get(0)).getName();

        DataRequestHandler dataRequestHandler = new DataRequestHandler();
        Path latestPagePath = Paths.get(requestedUri).resolve(release);
        Page page = dataRequestHandler.readAsPage(latestPagePath.toString(), true, zebedeeRequest);

        //TODO: Read navigaton from zebedee if zebedee request ????
        page.setNavigation(NavigationUtil.getNavigation());
        String html = TemplateService.getInstance().renderPage(page);
        return new BabbageStringResponse(html, CONTENT_TYPE);
    }

    private DirectoryListing readFromZebedee(String uri, ZebedeeRequest zebedeeRequest) throws ContentNotFoundException, IOException {

        // make request to browse api
        ZebedeeClient zebedeeClient = new ZebedeeClient(zebedeeRequest);
        DirectoryListing directoryListing;
        try {
            directoryListing = ContentUtil.deserialise(zebedeeClient.get("browse", uri, false), DirectoryListing.class);
        } finally {
            zebedeeClient.closeConnection();
        }

        return directoryListing;
    }


    private DirectoryListing readFromLocal(String requestedUri) throws IOException {
        return ContentUtil.listDirectory(Paths.get(requestedUri));
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
