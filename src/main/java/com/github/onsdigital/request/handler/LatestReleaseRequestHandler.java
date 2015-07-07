package com.github.onsdigital.request.handler;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.link.PageReference;
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
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.DOMException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

        List<PageReference> pageReferences;
        List<Page> pages = new ArrayList<>();

        if (zebedeeRequest != null) {
            pageReferences = readFromZebedee(requestedUri, zebedeeRequest);
        } else {
            pageReferences = readFromLocal(requestedUri);
        }

        DataRequestHandler dataRequestHandler = new DataRequestHandler();
        for (PageReference pageReference : pageReferences) {
            Page referencedPage = dataRequestHandler.readAsPage(pageReference.getUri().toString(), false, zebedeeRequest);
            pages.add(referencedPage);
        }

        sortPages(pages);
        Page page = pages.get(0);

        //TODO: Read navigaton from zebedee if zebedee request ????
        page.setNavigation(NavigationUtil.getNavigation());

        String html = TemplateService.getInstance().renderPage(page);
        return new BabbageStringResponse(html, CONTENT_TYPE);
    }


    private void sortPages(List<Page> pages) {
        Collections.sort(pages, new Comparator<Page>() {
            @Override
            public int compare(Page o1, Page o2) {
                if (o1 == null || o1.getDescription().getReleaseDate() == null) {
                    return -1;
                } else if (o2 == null || o2.getDescription().getReleaseDate() == null) {
                    return 1;
                } else {
                    return (o2.getDescription()).getReleaseDate()
                            .compareTo((o1.getDescription()).getReleaseDate());
                }
            }
        });
    }

    private List<PageReference> readFromZebedee(String uri, ZebedeeRequest zebedeeRequest) throws ContentNotFoundException, IOException {
        List<PageReference> pages = new ArrayList<>();

        // make request to browse api
        ZebedeeClient zebedeeClient = new ZebedeeClient(zebedeeRequest);
        try {
            DirectoryListing directoryListing = ContentUtil.deserialise(zebedeeClient.get("browse", uri, false), DirectoryListing.class);

            for (String folder : directoryListing.folders.keySet()) {
                pages.add(new PageReference(URI.create(uri + "/" + folder)));
            }
        } finally {
            zebedeeClient.closeConnection();
        }

        return pages;
    }


    private List<PageReference> readFromLocal(String requestedUri) throws IOException {
        Path taxonomyPath = FileSystems.getDefault().getPath(Configuration.getContentPath());
        Path path = taxonomyPath.resolve(StringUtils.removeStart(requestedUri, "/"));

        List<PageReference> pages = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path p : stream) {
                if (Files.isDirectory(p)) {
                    pages.add(new PageReference(URI.create(requestedUri + "/" + p.getFileName())));
                }
            }
            return pages;
        } catch (DOMException | MalformedURLException e) {
            throw new IOException("Error iterating taxonomy", e);
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    class DirectoryListing {
        public Map<String, String> folders = new HashMap<>();
        public Map<String, String> files = new HashMap<>();
    }
}
