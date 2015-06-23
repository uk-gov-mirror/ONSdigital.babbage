package com.github.onsdigital.request.handler;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.statistics.document.figure.table.Table;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.DataNotFoundException;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.data.zebedee.ZebedeeClient;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;
import com.github.onsdigital.template.TemplateService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
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

        String html = getHtml(requestedUri, zebedeeRequest);
        return new BabbageStringResponse(html, CONTENT_TYPE);
    }

    public String getHtml(String requestedUri, ZebedeeRequest zebedeeRequest) throws ContentNotFoundException, IOException {
        Page page;
        String tableHtml;

        if (zebedeeRequest != null) {
            page = ContentUtil.deserialisePage(readFromZebedee(requestedUri + ".json", zebedeeRequest));
            tableHtml = readFromZebedee(requestedUri + ".html", zebedeeRequest);
        } else {
            page = ContentUtil.deserialisePage(readFromLocalData(requestedUri));
            tableHtml = IOUtils.toString(getDataStream(requestedUri + ".html"));
        }

        if (page instanceof Table) {
            ((Table) page).setHtml(tableHtml);
        }

        return TemplateService.getInstance().renderPage(page);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    //Read from babbage's file system
    private InputStream readFromLocalData(String requestedUri) throws IOException {
        return DataService.getInstance().getDataStream(requestedUri);
    }

    //Read data from zebedee
    private String readFromZebedee(String uri, ZebedeeRequest zebedeeRequest) throws ContentNotFoundException, IOException {
        ZebedeeClient zebedeeClient = new ZebedeeClient(zebedeeRequest);
        try {
            return IOUtils.toString(zebedeeClient.readData(uri, false));
        } finally {
            zebedeeClient.closeConnection();
        }
    }

    public InputStream getDataStream(String uriString)
            throws IOException {
        uriString = StringUtils.removeStart(uriString, "/");
        System.out.println("Reading data under uri:" + uriString);
        Path taxonomy = FileSystems.getDefault().getPath(
                Configuration.getContentPath());

        // Look for a data.json file, or
        // fall back to adding a .json file extension
        Path data = taxonomy.resolve(uriString);

        if (Files.exists(data)) {
            return Files.newInputStream(data);
        }

        throw new DataNotFoundException(uriString);
    }
}
