package com.github.onsdigital.request.handler;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.data.DataNotFoundException;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageBinaryResponse;
import com.github.onsdigital.request.response.BabbageResponse;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

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
        System.out.println("Reading data under uri:" + uriPath);
        Path taxonomy = FileSystems.getDefault().getPath(
                Configuration.getContentPath());

        Path filePath = taxonomy.resolve(uriPath + ".png");

        if (Files.exists(filePath)) {
            return new BabbageBinaryResponse(Files.newInputStream(filePath), CONTENT_TYPE);
        }

        throw new DataNotFoundException(requestedUri);
    }


    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
