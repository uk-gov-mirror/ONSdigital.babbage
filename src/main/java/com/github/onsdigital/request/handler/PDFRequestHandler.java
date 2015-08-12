package com.github.onsdigital.request.handler;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.LocalFileDataService;
import com.github.onsdigital.request.response.BabbageBinaryResponse;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.util.PDFGenerator;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by bren on 07/07/15.
 */
public class PDFRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "pdf";


    public static final String CONTENT_TYPE = "application/pdf";

    public BabbageResponse get(String requestedUri, HttpServletRequest requests) throws Exception {
        String uriPath = StringUtils.removeStart(requestedUri, "/");
        System.out.println("Generating pdf for uri:" + uriPath);
        Path pdfFile = PDFGenerator.generatePdf(requestedUri, ContentUtil.deserialisePage(LocalFileDataService.getInstance().getDataStream(requestedUri)).getDescription().getTitle());
        InputStream fin = Files.newInputStream(pdfFile);
        BabbageBinaryResponse response = new BabbageBinaryResponse(fin, CONTENT_TYPE);
        response.addHeader("Content-Disposition", "attachment; filename=\"" + pdfFile.getFileName() + "\"");
        return response;
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }


}
