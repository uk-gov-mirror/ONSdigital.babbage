package com.github.onsdigital.babbage.pdf;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import java.io.IOException;
import java.io.InputStream;

public class HtmlImageReplacedElementFactory implements ReplacedElementFactory {

    private final ReplacedElementFactory superFactory;

    public HtmlImageReplacedElementFactory(ReplacedElementFactory superFactory) {
        this.superFactory = superFactory;
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

        String tagName = element.getTagName();

        // if we find an img tag
        if ("img".equals(tagName)) {

            // get the URL from the src attribute.
            String url = element.getAttribute("src");

            if (url != null && url.length() > 0) {
                try {

                    HttpClient client = HttpClientBuilder.create().build();
                    HttpResponse response = client.execute(new HttpGet(url));

                    try (InputStream input = response.getEntity().getContent()){
                        byte[] bytes = IOUtils.toByteArray(input);

                        Image image = Image.getInstance(bytes);
                        ITextFSImage fsImage = new ITextFSImage(image);

                        if (fsImage != null) {
                            if ((cssWidth != -1) || (cssHeight != -1)) {
                                fsImage.scale(cssWidth, cssHeight);
                            }
                            return new ITextImageElement(fsImage);
                        }
                    }

                } catch (IOException | BadElementException e) {
                    ExceptionUtils.getStackTrace(e);
                }
            }
        }

        return superFactory.createReplacedElement(layoutContext, blockBox, userAgentCallback, cssWidth, cssHeight);
    }

    @Override
    public void reset() {
        superFactory.reset();
    }

    @Override
    public void remove(Element e) {
        superFactory.remove(e);
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
        superFactory.setFormSubmissionListener(listener);
    }
}