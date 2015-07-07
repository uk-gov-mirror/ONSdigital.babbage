package com.github.onsdigital.request.handler;

import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.error.ResourceNotFoundException;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bren on 07/07/15.
 */
public class PDFRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "pdf";


    public static final String CONTENT_TYPE = "application/pdf";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return get(requestedUri, request, null);
    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {
        throw new ResourceNotFoundException();
        //TODO:
        /*String uriPath = StringUtils.removeStart(requestedUri, "/");
        System.out.println("Generating pdf for uri:" + uriPath);
        BabbageStringResponse pageResponse = (BabbageStringResponse) new PageRequestHandler().get(requestedUri, request);
        File pdfFile = PDFGenerator.htmlToPdf(pageResponse.getData(), "test.pdf");
        FileInputStream fin = FileUtils.openInputStream(pdfFile);
        return new BabbageBinaryResponse(fin, CONTENT_TYPE);*/
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }


}
