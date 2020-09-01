package com.github.onsdigital.babbage.pdf;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.highcharts.HighChartsExportClient;
import com.github.onsdigital.babbage.template.TemplateService;
import com.lowagie.text.Image;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Element;
import com.openhtmltopdf.extend.ReplacedElement;
import com.openhtmltopdf.extend.ReplacedElementFactory;
import com.openhtmltopdf.extend.UserAgentCallback;
import com.openhtmltopdf.layout.LayoutContext;
import com.openhtmltopdf.render.BlockBox;
import com.openhtmltopdf.pdfboxout.PdfBoxImage;
import com.openhtmltopdf.pdfboxout.PdfBoxImageElement;
import com.openhtmltopdf.pdfboxout.PdfBoxOutputDevice;
// import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Iterator;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;

public class ChartImageReplacedElementFactory implements ReplacedElementFactory {
    private PdfBoxOutputDevice outputDevice;

    public ChartImageReplacedElementFactory(PdfBoxOutputDevice outputDevice) {
        this.outputDevice = outputDevice;
    }   

    @Override
    public ReplacedElement createReplacedElement(
            LayoutContext layoutContext, BlockBox blockBox,
            UserAgentCallback userAgentCallback, int cssWidth, int cssHeight
    ) {
        Element element = blockBox.getElement();
        if (element == null) {
            return null;
        }

        // The markdown charts get output as a div with a particular class and data attributes for the URI.
        // Here we look for the class name in each DIV to see where charts need to be rendered.
        String nodeName = element.getNodeName();
        String className = element.getAttribute("class");
        if ("div".equals(nodeName) && className.contains("markdown-chart-div")) {
            String uri = element.getAttribute("data-uri");
            InputStream input = null;
            try {
                // read the chart JSON from the content service (zebedee reader)
                ContentResponse contentResponse = ContentClient.getInstance().getContent(uri);

                // The highcharts configuration is generated from a handlebars template with the chart JSON as input.
                LinkedHashMap<String, Object> additionalData = new LinkedHashMap<>();
                additionalData.put("width", 600);
                String chartConfig = TemplateService.getInstance().renderChartConfiguration(contentResponse.getDataStream(), additionalData);
                        
                Integer width = null; // do not set the width here as it overrides the scale
                double scale = 3.5; // we use the scale to increase the size of the chart so that it also increased the font size accordingly.
                input = HighChartsExportClient.getInstance().getImage(chartConfig, width, scale);

                byte[] bytes = IOUtils.toByteArray(input);
                PdfBoxImage fsImage = new PdfBoxImage(bytes, "");

                if (fsImage != null) {
                    if ((cssWidth != -1) || (cssHeight != -1)) {
                        fsImage.scale(cssWidth, cssHeight);
                    }
                    this.outputDevice.realizeImage(fsImage);
                    return new PdfBoxImageElement(element, fsImage, layoutContext.getSharedContext(), blockBox.getStyle().isImageRenderingInterpolate());
                }
            } catch (Exception ex) {
                error().exception(ex)
                        .log("ChartImageReplacedElementFactory.createReplacedElement encountered an unexpected error");
            } finally {
                IOUtils.closeQuietly(input);
            }
        }
        return null;
    }

    @Override
    public boolean isReplacedElement(Element e) {      
        if (e == null) {
            return false;
        }
        String nodeName = e.getNodeName();
        String className = e.getAttribute("class");

        boolean isReplaced = "div".equals(nodeName) && className.contains("markdown-chart-div");
        return isReplaced;
    }

}