package com.github.onsdigital.babbage.pdf;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.openhtmltopdf.extend.ReplacedElement;
import com.openhtmltopdf.extend.ReplacedElementFactory;
import com.openhtmltopdf.extend.UserAgentCallback;
import com.openhtmltopdf.layout.LayoutContext;
import com.openhtmltopdf.pdfboxout.PdfBoxImage;
import com.openhtmltopdf.pdfboxout.PdfBoxImageElement;
import com.openhtmltopdf.pdfboxout.PdfBoxOutputDevice;
import com.openhtmltopdf.render.BlockBox;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Element;

import java.io.InputStream;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

public class EquationImageInserter implements ReplacedElementFactory {

    private PdfBoxOutputDevice outputDevice;

    public EquationImageInserter(PdfBoxOutputDevice outputDevice) {
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
        if ("div".equals(nodeName) && className.contains("markdown-equation-div")) {

            String uri = element.getAttribute("data-uri");
            String filename = element.getAttribute("data-filename");

            InputStream input = null;
            try {
                info().data("uri", uri).data("filename", filename).log("inserting equation image into PDF");

                // read the chart JSON from the content service (zebedee reader)
                ContentResponse contentResponse = ContentClient.getInstance().getResource(uri + "/" + filename + ".png");

                try (InputStream inputStream = contentResponse.getDataStream()) {
                    byte[] bytes = IOUtils.toByteArray(inputStream);
                    PdfBoxImage fsImage = new PdfBoxImage(bytes, "");


                    if (fsImage != null) {
                        if ((cssWidth != -1) || (cssHeight != -1)) {
                            fsImage.scale(cssWidth, cssHeight);
                        }
                        this.outputDevice.realizeImage(fsImage);
                        return new PdfBoxImageElement(element, fsImage, layoutContext.getSharedContext(), blockBox.getStyle().isImageRenderingInterpolate());
                    }
                }

            } catch (Exception e) {
                error().exception(e).log("error inserting equation image into PDF");
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

        boolean isReplaced = "div".equals(nodeName) && className.contains("markdown-equation-div");
        return isReplaced;

    }

}