package com.github.onsdigital.request.handler;

import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.statistics.document.base.StatisticalDocument;
import com.github.onsdigital.content.partial.markdown.MarkdownSection;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;
import com.github.onsdigital.template.TemplateService;
import com.github.onsdigital.util.NavigationUtil;
import com.github.onsdigital.util.markdown.ChartTagReplacer;
import com.github.onsdigital.util.markdown.TableTagReplacer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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

        InjectCharts(page, zebedeeRequest);

        //TODO: Read navigaton from zebedee if zebedee request ????
        page.setNavigation(NavigationUtil.getNavigation());
        String html = TemplateService.getInstance().renderPage(page);
        return new BabbageStringResponse(html, CONTENT_TYPE);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    // TODO: find cleaner way of injecting charts + tables into markdown of bulletin / articles
    private void InjectCharts(Page page, ZebedeeRequest zebedeeRequest) throws IOException {
        if (page instanceof StatisticalDocument) {
            for (MarkdownSection markdownSection : ((StatisticalDocument) page).getSections()) {
                String markdown = markdownSection.getMarkdown();
                markdown = new TableTagReplacer().replaceCustomTags(markdown, zebedeeRequest);
                markdown = new ChartTagReplacer().replaceCustomTags(markdown, zebedeeRequest);
                markdownSection.setMarkdown(markdown);
            }
        }
    }
}
