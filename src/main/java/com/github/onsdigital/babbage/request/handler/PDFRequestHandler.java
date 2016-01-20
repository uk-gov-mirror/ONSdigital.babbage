package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageBinaryResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.github.onsdigital.babbage.pdf.PDFGenerator;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.github.onsdigital.babbage.content.client.ContentClient.filter;


/**
 * Created by bren on 07/07/15.
 */
public class PDFRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "pdf";


    public static final String CONTENT_TYPE = "application/pdf";

    public BabbageResponse get(String requestedUri, HttpServletRequest requests) throws Exception {
        String uriPath = StringUtils.removeStart(requestedUri, "/");
        System.out.println("Generating pdf for uri:" + uriPath);
        Path pdfFile = PDFGenerator.generatePdf(requestedUri, getTitle(requestedUri), RequestUtil.getAllCookies(requests));
        InputStream fin = Files.newInputStream(pdfFile);
        BabbageBinaryResponse response = new BabbageBinaryResponse(fin, CONTENT_TYPE);
        response.addHeader("Content-Disposition", "attachment; filename=\"" + pdfFile.getFileName() + "\"");
        return response;
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    public String getTitle(String uri) throws IOException, ContentReadException {
        ContentResponse contentResponse = ContentClient.getInstance().getContent(uri, filter(ContentFilter.TITLE));
        Map<String, Object> stringObjectMap = JsonUtil.toMap(contentResponse.getDataStream());
        return (String) stringObjectMap.get("title");
    }

}
