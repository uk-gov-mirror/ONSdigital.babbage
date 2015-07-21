package com.github.onsdigital.request.handler.highcharts;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.data.LocalFileDataService;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;

public class MarkdownChartConfigHandler implements RequestHandler {

    public static final String REQUEST_TYPE = "markdownchartconfig";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return get(requestedUri, request, null);
    }


    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {
        return new BabbageStringResponse(getChartConfig(requestedUri).toString());
    }

    //TODO: Read chart data from zebedee ?
    JsonObject getChartConfig(String requestedUri) throws IOException {

        JsonParser parser = new JsonParser();

        InputStreamReader reader = new InputStreamReader(LocalFileDataService.getInstance().getDataStream(requestedUri));
        JsonObject jsonObject = parser.parse(reader).getAsJsonObject();

        JsonObject config = parser.parse(Configuration.getLinechartConfig()).getAsJsonObject();
        return null;
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    public static void main(String[] args) {
        JsonObject jsonObject = new JsonParser().parse("{\"a\": \"A\"}").getAsJsonObject();
        System.out.println(new Gson().toJson(jsonObject));
    }

}

