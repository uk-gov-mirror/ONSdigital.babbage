package com.github.onsdigital.request.handler;

import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        return new BabbageStringResponse(getData(requestedUri, resolveReferences, null));
    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {
        boolean resolveReferences = request.getParameter("resolve") != null;
        return new BabbageStringResponse(getData(requestedUri, resolveReferences, zebedeeRequest));
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    public static String getData(String uri, boolean resolveReferences, ZebedeeRequest zebedeeRequest) throws ContentNotFoundException, IOException {

        DataService dataService = DataService.getInstance();
        try {
            Path dataPath = Paths.get(StringUtils.removeEnd(uri, "/")).resolve("data.json");
            return IOUtils.toString(dataService.readData(dataPath.toString(), resolveReferences, zebedeeRequest));
        } catch (ContentNotFoundException e) {
            return IOUtils.toString(dataService.readData(uri + ".json", resolveReferences, zebedeeRequest));
        }
    }
}
