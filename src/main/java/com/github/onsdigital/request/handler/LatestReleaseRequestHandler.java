package com.github.onsdigital.request.handler;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.content.DirectoryListing;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.response.BabbageRedirectResponse;
import com.github.onsdigital.request.response.BabbageResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Render a list page for the given URI.
 */
public class LatestReleaseRequestHandler implements RequestHandler {

    public static final String REQUEST_TYPE = "latest";
    public static final String CONTENT_TYPE = "text/html";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return get(requestedUri, request, null);
    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {

        //boolean jsEnhanced = PageRequestHandler.isJsEnhanced(request);
        //ContentRenderer pageRenderingService = new ContentRenderer(zebedeeRequest, jsEnhanced);

        Path latestPagePath = getLatestPagePath(requestedUri, zebedeeRequest);
        return new BabbageRedirectResponse(latestPagePath.toString());
        //return new BabbageStringResponse(pageRenderingService.renderPage(latestPagePath.toString()), CONTENT_TYPE);
    }

    public static Path getLatestPagePath(String requestedUri, ZebedeeRequest zebedeeRequest) throws ContentNotFoundException {
        DataService dataService = DataService.getInstance();
        DirectoryListing listing = dataService.readDirectory(requestedUri, zebedeeRequest);
        List<String> folders = new ArrayList<>(listing.folders.keySet());
        Collections.sort(folders, Collections.reverseOrder());
        String release = new File(folders.get(0)).getName();
        return Paths.get(requestedUri).resolve(release);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
