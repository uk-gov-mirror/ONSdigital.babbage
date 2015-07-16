package com.github.onsdigital.data;

import com.github.onsdigital.content.DirectoryListing;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.zebedee.ZebedeeClient;
import com.github.onsdigital.data.zebedee.ZebedeeDataService;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Route data requests to zebedee if required, else read from the local filesystem.
 */
public class DataService {

    private static DataService instance = new DataService();
    private DataService() { }
    public static DataService getInstance() {
        return instance;
    }

    public InputStream readData(String uri, boolean resolveReferences, ZebedeeRequest zebedeeRequest) throws ContentNotFoundException, IOException {
        if (zebedeeRequest != null) {
            return ZebedeeDataService.getInstance().readData(uri, zebedeeRequest, resolveReferences);
        }

        if (resolveReferences) {
            try {
                Page page = readAsPage(uri, true, zebedeeRequest);
                return IOUtils.toInputStream(page.toJson());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return LocalFileDataService.getInstance().readData(uri);
        }

        throw new DataNotFoundException(uri);
    }


    public Page readAsPage(String uri, boolean resolveReferences, ZebedeeRequest zebedeeRequest) throws IOException, ContentNotFoundException {
        if (zebedeeRequest != null) {
            return ContentUtil.deserialisePage(ZebedeeDataService.getInstance().readData(uri, zebedeeRequest, resolveReferences));
        } else {
            Page page = ContentUtil.deserialisePage(LocalFileDataService.getInstance().readData(uri));
            if (resolveReferences) {
                page.loadReferences(LocalFileDataService.getInstance());
            }
            return page;
        }
    }

    public DirectoryListing readDirectory(String uri, ZebedeeRequest zebedeeRequest) throws ContentNotFoundException {

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
}
