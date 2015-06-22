package com.github.onsdigital.request.handler;

import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.data.zebedee.ZebedeeClient;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageBinaryResponse;
import com.github.onsdigital.request.response.BabbageResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Serves rendered html output
 */
public class ImageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "image";

    public static final String CONTENT_TYPE = "image/png";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return get(requestedUri, request, null);
    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {

        String uriPath = StringUtils.removeStart(requestedUri, "/");
        System.out.println("Reading image under uri:" + uriPath);


        String imagePath = uriPath + ".png";

        if (zebedeeRequest != null) {
            return new BabbageBinaryResponse(readFromZebedee(imagePath, zebedeeRequest, false), CONTENT_TYPE);
        } else {
            return new BabbageBinaryResponse(readFromLocalData(imagePath), CONTENT_TYPE);
        }
    }

    //Read from babbage's file system
    private InputStream readFromLocalData(String requestedUri) throws IOException {
        return DataService.getInstance().getDataStream(requestedUri);
    }

    //Read data from zebedee
    private InputStream readFromZebedee(String uri, ZebedeeRequest zebedeeRequest, boolean resolveReferences) throws ContentNotFoundException, IOException {
        ZebedeeClient zebedeeClient = new ZebedeeClient(zebedeeRequest);
        try {
            // copy the contents of the input stream. The stream was being closed prematurely.
            return new ByteArrayInputStream(IOUtils.toByteArray(zebedeeClient.readData(uri, resolveReferences)));
        } finally {
            zebedeeClient.closeConnection();
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
