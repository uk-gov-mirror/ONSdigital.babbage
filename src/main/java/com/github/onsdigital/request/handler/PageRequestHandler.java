package com.github.onsdigital.request.handler;

import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.template.TemplateService;
import com.github.onsdigital.util.NavigationUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Serves rendered html output
 */
public class PageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "/";

    public static final String CONTENT_TYPE = "text/html";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return get(requestedUri, request, null);

    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {
        DataRequestHandler dataRequestHandler = new DataRequestHandler();
        Page page = dataRequestHandler.readAsPage(requestedUri, true, zebedeeRequest);

        //TODO: Read navigaton from zebedee if zebedee request ????
        page.setNavigation(NavigationUtil.getNavigation());
        String html = TemplateService.getInstance().renderPage(page);
        return new BabbageResponse(html, CONTENT_TYPE);
    }


    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
