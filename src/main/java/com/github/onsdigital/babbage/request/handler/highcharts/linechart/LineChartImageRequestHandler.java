package com.github.onsdigital.babbage.request.handler.highcharts.linechart;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.highcharts.HighChartsExportClient;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedBinaryResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.github.onsdigital.babbage.util.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

/**
 * Created by bren on 18/06/15.
 */
public class LineChartImageRequestHandler extends BaseRequestHandler {

    public static final String REQUEST_TYPE = "linechartimage";
    public static final String CONTENT_TYPE = "image/png";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        info().data("uri", requestedUri).log("generating linechart image for uri");
        Map<String, String[]> queryParameters = ContentClient.filter(ContentFilter.SERIES);
        queryParameters.putAll(RequestUtil.getQueryParameters(request));
        ContentResponse series = ContentClient.getInstance().getContent(requestedUri, queryParameters);
        ContentResponse description = ContentClient.getInstance().getContent(requestedUri, ContentClient.filter(ContentFilter.DESCRIPTION));
        Map<String, Object> descriptionMap = new HashMap<>();
        descriptionMap.put("fullDescription", JsonUtil.toMap(description.getDataStream()));
        String config = TemplateService.getInstance().renderTemplate("highcharts/config/linechartconfig", series.getDataStream(), descriptionMap);
        try (InputStream stream = HighChartsExportClient.getInstance().getImage(config, null)) {
            return new BabbageContentBasedBinaryResponse(series, stream, CONTENT_TYPE);
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
