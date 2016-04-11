package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.pdf.PdfGeneratorLegacy;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageBinaryResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.babbage.content.client.ContentClient.filter;


/**
 * Created by bren on 07/07/15.
 */
public class PdfLegacyRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "pdf";
    public static final String CONTENT_TYPE = "application/pdf";

    public BabbageResponse get(String requestedUri, HttpServletRequest requests) throws Exception {
        String uriPath = StringUtils.removeStart(requestedUri, "/");
        System.out.println("Generating pdf for uri:" + uriPath);
        String pdfTable = getPDFTables(uriPath);
        if(pdfTable != null) {
            System.out.println("Using pdfTable: " + pdfTable);
        }
        Path pdfFile = PdfGeneratorLegacy.generatePdf(requestedUri, getTitle(requestedUri), RequestUtil.getAllCookies(requests), pdfTable);
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
        ContentResponse contentResponse = ContentClient.getInstance().getContent(uri, filter(ContentFilter.DESCRIPTION));
        Map<String, Object> stringObjectMap = JsonUtil.toMap(contentResponse.getDataStream());

        String title = (String) stringObjectMap.get("title");
        String edition = (String) stringObjectMap.get("edition");

        if (StringUtils.isNotEmpty(edition))
            title += " " + edition;

        return title;
    }

    public String getPDFTables(String uri) throws IOException, ContentReadException {
        ContentResponse contentResponse = ContentClient.getInstance().getContent(uri);
        Map<String, Object> stringObjectMap = JsonUtil.toMap(contentResponse.getDataStream());

        List<Map<String, Object>> o = (List<Map<String, Object>>) stringObjectMap.get("pdfTable");

        if(o != null && o.size() > 0) {
            String filename = (String)o.get(0).get("file");
            String file = uri + "/" + filename;
            ContentResponse pdfTableContentResponse = ContentClient.getInstance().getResource(file);
            File targetFile = new File(FileUtils.getTempDirectoryPath() + "/" + filename);
            FileUtils.copyInputStreamToFile(pdfTableContentResponse.getDataStream(), targetFile);
            return targetFile.getAbsolutePath();
        }

        return null;
    }

}
