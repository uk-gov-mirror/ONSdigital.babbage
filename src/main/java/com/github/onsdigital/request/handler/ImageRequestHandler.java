package com.github.onsdigital.request.handler;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.data.DataNotFoundException;
import com.github.onsdigital.data.zebedee.ZebedeeClient;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.response.BabbageBinaryResponse;
import com.github.onsdigital.request.response.BabbageResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "image";

    public static final String CONTENT_TYPE = "image/png";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return new BabbageBinaryResponse(null, CONTENT_TYPE);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
