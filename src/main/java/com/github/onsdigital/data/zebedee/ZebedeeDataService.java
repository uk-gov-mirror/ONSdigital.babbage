package com.github.onsdigital.data.zebedee;

import com.github.onsdigital.content.DirectoryListing;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.content.util.ContentUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class ZebedeeDataService {

    private static ZebedeeDataService instance = new ZebedeeDataService();
    private ZebedeeDataService() { }
    public static ZebedeeDataService getInstance() {
        return instance;
    }

    public InputStream readData(String uri, ZebedeeRequest zebedeeRequest, boolean resolveReferences) throws IOException, ContentNotFoundException {
        ZebedeeClient zebedeeClient = new ZebedeeClient(zebedeeRequest);
        try {
            // consume the returned input stream as its closed when the zebedee client connection is closed.
            String dataUri = uri;

            if (StringUtils.isEmpty(FilenameUtils.getExtension(dataUri))) {
                dataUri = Paths.get(uri).resolve("data.json").toString();
            }

            try {
                return IOUtils.toInputStream(IOUtils.toString(zebedeeClient.readData(dataUri, resolveReferences)));
            } catch (ContentNotFoundException e) {
                dataUri = uri + ".json";
                return IOUtils.toInputStream(IOUtils.toString(zebedeeClient.readData(dataUri, resolveReferences)));
            }
        } finally {
            zebedeeClient.closeConnection();
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
