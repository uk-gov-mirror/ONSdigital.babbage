package com.github.onsdigital.babbage.pdf;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.openhtmltopdf.extend.ReplacedElement;
import com.openhtmltopdf.extend.ReplacedElementFactory;
import com.openhtmltopdf.extend.UserAgentCallback;
import com.openhtmltopdf.layout.LayoutContext;
import com.openhtmltopdf.pdfboxout.PdfBoxImage;
import com.openhtmltopdf.pdfboxout.PdfBoxImageElement;
import com.openhtmltopdf.pdfboxout.PdfBoxOutputDevice;
import com.openhtmltopdf.render.BlockBox;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.slf4j.LoggerFactory.getLogger;

public class HtmlImageReplacedElementFactory implements ReplacedElementFactory {

    private PdfBoxOutputDevice outputDevice;

    private static final Logger log = getLogger(HtmlImageReplacedElementFactory.class);

    public HtmlImageReplacedElementFactory(PdfBoxOutputDevice outputDevice) {
        this.outputDevice = outputDevice;
    }

    private final double scale = 16f;
    private final int maxWidth = 14000;

    @Override
    public ReplacedElement createReplacedElement(
            LayoutContext layoutContext, BlockBox blockBox,
            UserAgentCallback userAgentCallback, int cssWidth, int cssHeight
    ) {

        Element element = blockBox.getElement();
        if (element == null) {
            return null;
        }

        String tagName = element.getTagName();

        // if we find an img tag
        if ("img".equals(tagName)) {

            // get the URL from the src attribute.
            String src = element.getAttribute("src");
            try {


                if (src != null && src.length() > 0) {

                    // check if its a relative url - if so get the resource from the content service (zebedee)
                    URI uri = new URI(src);
                    if (!uri.isAbsolute()) {
                        ContentResponse contentResponse = ContentClient.getInstance().getResource(src);

                        try (InputStream input = contentResponse.getDataStream())
                        {
                            PdfBoxImage fsImage = getReplacedImage(cssWidth, cssHeight, input);
                            if (fsImage != null) {
                                this.outputDevice.realizeImage(fsImage);
                                return new PdfBoxImageElement(element, fsImage, layoutContext.getSharedContext(), blockBox.getStyle().isImageRenderingInterpolate());
                            }
                        }
                    }

                    // if the url is absolute, go get it using HTTP client.
                    HttpClient client = HttpClientBuilder.create().build();
                    HttpResponse response = client.execute(new HttpGet(src));

                    try (InputStream input = response.getEntity().getContent()) {
                        PdfBoxImage fsImage = getReplacedImage(cssWidth, cssHeight, input);
                        if (fsImage != null) {
                            this.outputDevice.realizeImage(fsImage);
                            return new PdfBoxImageElement(element, fsImage, layoutContext.getSharedContext(), blockBox.getStyle().isImageRenderingInterpolate());
                        }
                    }
                }

            } catch (URISyntaxException | ContentReadException | IOException | BadElementException e) {
                log.error(e.getMessage());
            }


        }

        return null;
    }

    private PdfBoxImage getReplacedImage(int cssWidth, int cssHeight, InputStream input) throws IOException, BadElementException {
        byte[] bytes = IOUtils.toByteArray(input);

        Image image = Image.getInstance(bytes);
        PdfBoxImage fsImage = new PdfBoxImage(bytes, "");

        if (fsImage != null) {
            if ((cssWidth != -1) || (cssHeight != -1)) {
                fsImage.scale(cssWidth, cssHeight);
            } else {

                if ((fsImage.getWidth() * scale) > maxWidth) {
                    // pass -1 as height to maintain aspect ratio when setting width.
                    fsImage.scale(maxWidth, -1);
                } else {
                    fsImage.scale(new Double(fsImage.getWidth() * scale).intValue(), new Double(fsImage.getHeight() * scale).intValue());
                }
            }
            return fsImage;
        }
        return null;
    }

    @Override
    public boolean isReplacedElement(Element e) {
        if (e == null) {
            return false;
        }

        String tagName = e.getTagName();

        boolean isReplaced = "img".equals(tagName);
        return isReplaced;

    }

}