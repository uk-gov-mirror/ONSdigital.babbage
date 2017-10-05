package com.github.onsdigital.babbage.highcharts;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.logging.Log;
import com.github.onsdigital.babbage.response.BabbageContentBasedBinaryResponse;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;
import java.awt.Graphics;

import javax.imageio.ImageIO;

import java.awt.*;

import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.TITLE_PARAM;
import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.URI_PARAM;
import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.WIDTH_PARAM;
import static java.text.MessageFormat.format;

import java.awt.image.BufferedImage;

/**
 * Created by bren on 09/10/15.
 */
public class ChartRenderer {

    static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    static final String CONTENT_DISPOSITION_HEADER_FMT = "attachment; filename=\"{0}.png\"";
    static final String DEFAULT_CONTENT_DISPOSITION_HEADER_FMT = "attachment; filename=\"Chart-{0}.png\"";
    static final String HIGHCHARTS_TEMPLATE = "highcharts/chart";
    static final String EMBEDED_HIGHCHARTS_TEMPLATE = "partials/highcharts/embeddedchart";
    static final String PNG_MIME_TYPE = "image/png";
    static final String DATA_PARAM = "data";
    static final String DEFAULT_TITLE_VALUE = "[Title]";
    public static final int DEFAULT_CHART_WIDTH = 700;
    public static final int MAX_CHART_WIDTH = 1600;
    private static final ObjectMapper mapper = new ObjectMapper();

    private static ChartRenderer instance = new ChartRenderer();
    private ContentClient contentClient = ContentClient.getInstance();
    private TemplateService templateService = TemplateService.getInstance();
    private HighChartsExportClient highChartsExportClient = HighChartsExportClient.getInstance();

    public static ChartRenderer getInstance() {
        return instance;
    }

    // Singleton use getInstance method instead.
    private ChartRenderer() {
    }

    /**
     * Fetches configuration from Zebedee Reader and converts it into chart configuration and renders using Handlebars templates for the chart type.
     * <p>
     * Optionally takes a width parameter, width is 700 by default, if width exceeds max, max width will be applied, if it is smaller than min, min width will apply
     */
    public void renderChartConfig(HttpServletRequest request, HttpServletResponse response) throws IOException, ContentReadException {
        String uri = request.getParameter(URI_PARAM);
        if (assertUri(uri, request, response)) {
            ContentResponse contentResponse = contentClient.getContent(uri);
            Map<String, Object> additionalData = new ChartConfigBuilder().width(getWidth(request)).getMap();

            try (InputStream inputStream = contentResponse.getDataStream()) {
                String chartConfig = templateService.renderChartConfiguration(inputStream, additionalData);
                new BabbageContentBasedStringResponse(contentResponse, chartConfig).apply(request, response);
            }
        }
    }

    /**
     * Fetches configuration from Zebedee Reader and renders self contained chart html
     * <p>
     * Optionally takes a width parameter, width is 600 by default, if width exceeds max, max width will be applied, if it is smaller than min, min width will apply
     */
    public void renderEmbeddedChart(HttpServletRequest request, HttpServletResponse response) throws IOException, ContentReadException {
        String uri = request.getParameter(URI_PARAM);
        if (assertUri(uri, request, response)) {
            ContentResponse contentResponse = contentClient.getContent(uri);
            Map<String, Object> additionData = new ChartConfigBuilder()
                    .width(getWidth(request))
                    .showTitle(request)
                    .showSubTitle(request)
                    .showSource(request)
                    .showNotes(request)
                    .getMap();

            try (InputStream inputStream = contentResponse.getDataStream()) {
                String renderedTemplate = templateService.renderTemplate(EMBEDED_HIGHCHARTS_TEMPLATE, inputStream,
                        additionData);
                new BabbageContentBasedStringResponse(contentResponse, renderedTemplate, MediaType.TEXT_HTML)
                        .applyEmbedded(request, response);
            }
        }
    }

    public void renderChartImage(HttpServletRequest request, HttpServletResponse response) throws IOException, ContentReadException {
        String uri = request.getParameter(URI_PARAM);
        if (assertUri(uri, request, response)) {
            ContentResponse contentResponse = contentClient.getContent(uri);
            String jsonRequest = contentResponse.getAsString();
            Map<String, Object> json = (Map<String, Object>)templateService.sanitize(jsonRequest);
            Integer width = getWidth(request);

            if (json.get("chartType").equals("small-multiples")) {
                renderMultipleChartImage(request, response, uri, json, width, contentResponse);
                return;
            }

            InputStream imageInputStream = renderSingleChartImage(jsonRequest, width);
            BabbageResponse babbabeResp = new BabbageContentBasedBinaryResponse(contentResponse, imageInputStream, PNG_MIME_TYPE);
            babbabeResp.addHeader(CONTENT_DISPOSITION_HEADER, getImageContentDispositionHeader(uri, jsonRequest));
            babbabeResp.apply(request, response);

        }
    }

    private void renderMultipleChartImage(HttpServletRequest request, HttpServletResponse response, String uri, Map<String, Object> json, Integer outputWidth, ContentResponse contentResponse) throws IOException {
        List<String> series = (List<String>)json.get("series");
        Integer padding = 10;
        Integer columns = 3;
        Integer chartWidth = (outputWidth-(padding*(columns+1))) / columns;
        Integer rows = (int)Math.ceil((float)series.size() / (float)columns);
        List<Map<String, Object>> originalData = (List<Map<String, Object>>)json.get("data");
        Map<String, Map<String, Object>> charts = new HashMap<>();
        ArrayList<BufferedImage> chartImages = new ArrayList<>();
        ArrayList<BufferedImage> chartTitles = new ArrayList<>();
        int[] rowHeights = new int[rows];
        int[] titleHeights = new int[rows];
        BufferedImage mainTitle = renderImageText(json.get("title").toString(), outputWidth, 21);

        for (Integer i = 0; i < series.size(); i++) {
            String title = series.get(i);
            BufferedImage chartTitle = renderImageText(title, chartWidth);
            chartTitles.add(chartTitle);
            Integer row = i/columns;
            
            if (titleHeights[row] < chartTitle.getHeight()) {
                titleHeights[row] = chartTitle.getHeight();
            }
        }

        System.out.println(Arrays.toString(titleHeights));

        for (String chart : series) {
            Map<String, Object> chartData = new HashMap<String, Object>();
            
            for (Map.Entry<String, Object> entry : json.entrySet()) {
                String key = entry.getKey();
                if (key.equals("data") || key.equals("headers") || key.equals("series") || key.equals("chartType") || key.equals("title")) {
                    continue;
                }
                chartData.put(key, entry.getValue());
            }
            
            ArrayList<Map<String, Object>> data = new ArrayList<>();
            for (Map<String, Object> point : originalData) {
                HashMap<String, Object> editedData = new HashMap<>();
                editedData.put("", point.get(""));
                editedData.put("date", point.get("date"));
                editedData.put("label", point.get("label"));
                editedData.put(chart, point.get(chart));
                data.add(editedData);
            }
            chartData.put("data", data);
            chartData.put("headers", new ArrayList<>(Arrays.asList("", chart)));
            chartData.put("series", new ArrayList<>(Arrays.asList(chart)));
            chartData.put("chartType", "line");
            chartData.put("isSmallMultiple", true);
            chartData.put("title", null);
            charts.put(chart, chartData);
        }

        for (Integer i = 0; i < series.size(); i++) {
            String chart = series.get(i);
            Map<String, Object> chartData = charts.get(chart);
            Gson gson = new Gson();
            String chartString = gson.toJson(chartData);
            InputStream imageInputStream = renderSingleChartImage(chartString, chartWidth);
            BufferedImage bufferedImage = ImageIO.read(imageInputStream);

            chartImages.add(bufferedImage);

            Integer row = i/columns;

            if (rowHeights[row] < bufferedImage.getHeight()) {
                rowHeights[row] = bufferedImage.getHeight();
            }
        }

        Integer outputHeight = IntStream.of(rowHeights).sum() + IntStream.of(titleHeights).sum() + mainTitle.getHeight() + padding;
        BufferedImage result = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = result.getGraphics();
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, outputWidth, outputHeight);

        graphics.drawImage(mainTitle, 0, 0, null);

        Integer rowPosition = 0, colPosition = 0;
        Integer yPosition = titleHeights[0] + mainTitle.getHeight() + padding;
        Integer titleYPosition = mainTitle.getHeight() + padding;

        for (Integer i = 0; i < series.size(); i++) {
            Integer xPosition = (colPosition * chartWidth) + ((colPosition + 1) * padding);

            if (i % 3 == 0 && i / 3 > 0) {
                yPosition += (rowHeights[i/3-1] + titleHeights[i/3]);
                titleYPosition += (rowHeights[i/3-1] + titleHeights[i/3-1]);
            }
            
            graphics.drawImage(chartTitles.get(i), xPosition, titleYPosition, null);
            graphics.drawImage(chartImages.get(i), xPosition, yPosition, null);
            
            if (colPosition == columns-1) {
                colPosition = 0;
                rowPosition++;
            } else {
                colPosition++;
            }
        }
        
        ImageIO.write(result, "png", new File("result.png"));
    }

    private static BufferedImage renderImageText(String text, Integer width) throws IOException {
        return renderImageText(text, width, 14);
    }

    private static BufferedImage renderImageText(String text, Integer width, Integer fontSize) throws IOException {
        Integer height = 600;
        Integer padding = 10;
        Integer lineSpacing = 7;
        Integer textSpace = width - (2 * padding);

        BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = i.createGraphics();

        Font f = new Font("TimesRoman", Font.PLAIN, fontSize);
        g.setFont(f);
        g.setPaint(Color.black);
        FontMetrics fm = g.getFontMetrics();

        ArrayList<String> lines = new ArrayList<String>();
        String[] words = text.split(" ");

        String buffer = "";
        for(String word : words) {
            String tempBuffer = buffer;

            if(tempBuffer.length() > 0) {
                tempBuffer += " ";
            }
            tempBuffer += word;

            if (fm.stringWidth(tempBuffer) > textSpace) {
                lines.add(buffer);
                buffer = word;
                continue;
            }

            buffer = tempBuffer;
        }

        if(buffer.length() > 0) {
            lines.add(buffer);
        }

        Integer lineHeight = g.getFontMetrics().getAscent();
        Integer y = lineHeight + padding;
        for(String line : lines) {
            g.drawString(line, padding, y);
            y += lineHeight + lineSpacing;
        }

        Integer titleHeight = (lineHeight * lines.size()) + (padding * lines.size()+1) + fm.getDescent();

        BufferedImage croppedTitleImage = new BufferedImage(width, titleHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = croppedTitleImage.getGraphics();

        graphics.drawImage(i, 0, 0, null);

        return croppedTitleImage;
    }
    
    private InputStream renderSingleChartImage(String jsonRequest, Integer width) throws IOException {
        Map<String, Object> additionalData = new ChartConfigBuilder().width(width).getMap();
        String chartConfig;
        chartConfig = templateService.renderChartConfiguration(jsonRequest, additionalData);

        return highChartsExportClient.getImage(chartConfig, width);
    }

    private String getImageContentDispositionHeader(String uri, String jsonRequest) throws IOException {
        Map<String, Object> json = (Map<String, Object>)templateService.sanitize(jsonRequest);
        String chartTitle = (String) json.get(TITLE_PARAM);
        if (StringUtils.isEmpty(chartTitle) || StringUtils.equalsIgnoreCase(chartTitle, DEFAULT_TITLE_VALUE)) {
            return format(DEFAULT_CONTENT_DISPOSITION_HEADER_FMT, Paths.get(uri).getFileName().toString());
        }
        return format(CONTENT_DISPOSITION_HEADER_FMT, chartTitle);
    }

    /**
     * Converts given data into chart configuration and renders using Handlebars templates for the chart type.
     * <p>
     * Optionally takes a width parameter, width is 600 by default, if width exceeds max, max width will be applied, if it is smaller than min, min width will apply
     */
    public void renderChartConfigFor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String data = request.getParameter(DATA_PARAM);
        if (StringUtils.isEmpty(data)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            IOUtils.write("Please specify chart data to get chart configuration for", response.getOutputStream());
        }
        Map<String, Object> additionalData = new ChartConfigBuilder().width(getWidth(request)).getMap();
        String renderedTemplate = templateService.renderChartConfiguration(data, additionalData);
        new BabbageStringResponse(renderedTemplate).apply(request, response);
    }

    /**
     * Fetches configuration from Zebedee Reader and renders self contained chart html
     * <p>
     * Optionally takes a width parameter, width is 600 by default, if width exceeds max, max width will be applied, if it is smaller than min, min width will apply
     */
    public void renderChart(HttpServletRequest request, HttpServletResponse response) throws IOException, ContentReadException {
        String uri = request.getParameter(URI_PARAM);
        if (assertUri(uri, request, response)) {
            ContentResponse contentResponse = contentClient.getContent(uri);
            Map<String, Object> additionalData = new ChartConfigBuilder().width(getWidth(request)).getMap();

            try (InputStream inputStream = contentResponse.getDataStream()) {
                String renderedTemplate = templateService.renderTemplate(HIGHCHARTS_TEMPLATE, inputStream, additionalData);
                new BabbageContentBasedStringResponse(contentResponse, renderedTemplate, MediaType.TEXT_HTML)
                        .apply(request, response);
            }
        }
    }

    private boolean assertUri(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (StringUtils.isEmpty(uri)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            IOUtils.write("Please specify uri of the chart", response.getOutputStream());
            return false;
        }
        return true;
    }

    public static Integer getWidth(HttpServletRequest request) {
        try {
            String width = request.getParameter(WIDTH_PARAM);
            if (StringUtils.isNotEmpty(width)) {
                return calculateWidth(Integer.parseInt(width));
            }
        } catch (NumberFormatException e) {
            Log.build("Chart width not a valid number, default width will be used.", Level.DEBUG).log();
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
