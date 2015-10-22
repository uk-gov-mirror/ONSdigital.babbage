package com.github.onsdigital.babbage.api.endpoint.chart;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.github.onsdigital.babbage.highcharts.ChartRenderer.DEFAULT_CHART_WIDTH;
import static com.github.onsdigital.babbage.highcharts.ChartRenderer.MAX_CHART_WIDTH;

/**
 * Created by bren on 13/10/15.
 */
class ChartRequestUtil {

    public static Integer getWidth(HttpServletRequest request) {
        try {
            String width = request.getParameter("width");
            if (StringUtils.isNotEmpty(width)) {
                return calculateWidth(Integer.parseInt(width));
            }
        } catch (NumberFormatException e) {
        }
        return DEFAULT_CHART_WIDTH;
    }

    private static int calculateWidth(Integer width) {
        int w = width == null ? DEFAULT_CHART_WIDTH : width;
        if (w < 0) {
            w = DEFAULT_CHART_WIDTH;
        } else if (w > MAX_CHART_WIDTH) {
            w = MAX_CHART_WIDTH;
        }
        return w;
    }


}
