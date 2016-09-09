package com.github.onsdigital.babbage.request.handler.highcharts.linechart;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bren on 18/06/15.
 */
public class LineChartConfigRequestHandler extends BaseRequestHandler {

    public static final String REQUEST_TYPE = "linechartconfig";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        ContentResponse series = ContentClient.getInstance().getContent(requestedUri, ContentClient.filter(ContentFilter.SERIES));
        ContentResponse description = ContentClient.getInstance().getContent(requestedUri, ContentClient.filter(ContentFilter.DESCRIPTION));
        Map<String, Object> descriptionMap = new HashMap<>();
        descriptionMap.put("fullDescription", JsonUtil.toMap(description.getDataStream()));
        String config = TemplateService.getInstance().renderTemplate("highcharts/config/linechartconfig", series.getDataStream(), descriptionMap);
        return new BabbageContentBasedStringResponse(series, config);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
