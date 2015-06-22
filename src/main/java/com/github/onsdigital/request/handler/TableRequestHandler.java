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
 * Renders table html from a predefined xls file.
 */
public class TableRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "table";
    public static final String CONTENT_TYPE = "text/html";

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

//        File xlsFile = new File(uri);
//        Document document = XlsToHtmlConverter.convert(path.toFile());
//
//        // When the toString method is called.
//        String output = XlsToHtmlConverter.docToString(document);
//
//
//        // Write the file to the response
//        try (InputStream input = Files.newInputStream(path)) {
//            org.apache.commons.io.IOUtils.copy(new StringReader(output),
//                    response.getOutputStream());
//        }
//
//



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
