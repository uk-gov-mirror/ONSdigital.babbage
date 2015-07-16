package com.github.onsdigital.page;

import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.statistics.document.figure.table.Table;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.content.service.ContentRenderingService;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.data.LocalFileDataService;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.template.TemplateService;
import com.github.onsdigital.util.NavigationUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class ContentRenderer implements ContentRenderingService {

    ZebedeeRequest zebedeeRequest;

    public ContentRenderer(ZebedeeRequest zebedeeRequest) {
        this.zebedeeRequest = zebedeeRequest;
    }

    @Override
    public String renderChart(String uri, boolean partial) throws IOException, ContentNotFoundException {
        Page page = DataService.getInstance().readAsPage(uri, false, zebedeeRequest);

        String template = "chart";

        if (partial) {
            template = "partials/" + template;
        }

        return TemplateService.getInstance().render(page, template);
    }

    @Override
    public String renderTable(String uri, boolean partial) throws IOException, ContentNotFoundException {

        DataService dataService = DataService.getInstance();

        Page page = dataService.readAsPage(uri + ".json", false, zebedeeRequest);
        String tableHtml = IOUtils.toString(dataService.readData(uri + ".html", false, zebedeeRequest));

        if (page instanceof Table) {
            ((Table) page).setHtml(tableHtml);
        }

        String template = "table";

        if (partial) {
            template = "partials/" + template;
        }

        return TemplateService.getInstance().render(page, template);
    }

    public String renderPage(String uri) throws IOException, ContentNotFoundException {
        Page page = DataService.getInstance().readAsPage(uri, true, zebedeeRequest);
        page.processContent(LocalFileDataService.getInstance(), this);
        page.setNavigation(NavigationUtil.getNavigation());
        return TemplateService.getInstance().renderPage(page);
    }
}
