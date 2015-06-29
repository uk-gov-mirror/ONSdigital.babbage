package com.github.onsdigital.request.handler;

import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.data.zebedee.ZebedeeClient;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Handle data requests. Diverts data requests to Zebedee if Florence is logged on on client machine
 */
public class DataRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "data";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        boolean resolveReferences = request.getParameter("resolve") != null;
        return new BabbageStringResponse(read(requestedUri, resolveReferences, null));
    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {
        boolean resolveReferences = request.getParameter("resolve") != null;
        return new BabbageStringResponse(read(requestedUri, resolveReferences, zebedeeRequest));
    }


    private String read(String requestedUri, boolean resolveReferences, ZebedeeRequest zebedeeRequest) throws IOException, ContentNotFoundException {

        if (zebedeeRequest != null) {
            return readFromZebedee(requestedUri, zebedeeRequest, resolveReferences);
        }

        if (resolveReferences) {
            Page page = readAsPage(requestedUri, true, null);
            return page.toJson();
        } else {
            return IOUtils.toString(readFromLocalData(requestedUri));
        }
    }

    public Page readAsPage(String requestedUri, boolean resolveReferences, ZebedeeRequest zebedeeRequest) throws IOException, ContentNotFoundException {
        if (zebedeeRequest != null) {
            return ContentUtil.deserialisePage(readFromZebedee(requestedUri, zebedeeRequest, resolveReferences));
        } else {
            Page page = ContentUtil.deserialisePage(readFromLocalData(requestedUri));
            if (resolveReferences) {
                page.loadReferences(DataService.getInstance());
            }
            return page;
        }
    }

    //Read from babbage's file system
    private InputStream readFromLocalData(String requestedUri) throws IOException {
        return DataService.getInstance().getDataStream(requestedUri);
    }

    //Read data from zebedee
    private String readFromZebedee(String uri, ZebedeeRequest zebedeeRequest, boolean resolveReferences) throws IOException, ContentNotFoundException {
        ZebedeeClient zebedeeClient = new ZebedeeClient(zebedeeRequest);
        try {
            try {
                return IOUtils.toString(zebedeeClient.readData(uri + "/data.json", resolveReferences));
            } catch (ContentNotFoundException e) {
                return IOUtils.toString(zebedeeClient.readData(uri + ".json", resolveReferences));
            }
        } finally {
            zebedeeClient.closeConnection();
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
