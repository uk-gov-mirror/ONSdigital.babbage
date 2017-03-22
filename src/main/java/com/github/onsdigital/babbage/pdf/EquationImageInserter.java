package com.github.onsdigital.babbage.pdf;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.logging.Log;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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

public class EquationImageInserter implements ReplacedElementFactory {

    private final ReplacedElementFactory superFactory;

    public EquationImageInserter(ReplacedElementFactory superFactory) {
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

        // The markdown charts get output as a div with a particular class and data attributes for the URI.
        // Here we look for the class name in each DIV to see where charts need to be rendered.
        String nodeName = element.getNodeName();
        String className = element.getAttribute("class");
        if ("div".equals(nodeName) && className.contains("markdown-equation-div")) {

            String uri = element.getAttribute("data-uri");
            String filename = element.getAttribute("data-filename");

            InputStream input = null;
            try {

                Log.buildDebug("Inserting equation image into PDF")
                        .addParameter("uri", uri)
                        .addParameter("filename", filename)
                        .log();

                // read the chart JSON from the content service (zebedee reader)
                ContentResponse contentResponse = ContentClient.getInstance().getResource(uri + "/" + filename + ".png");

                try ( InputStream inputStream = contentResponse.getDataStream() ){
                    byte[] bytes = IOUtils.toByteArray(inputStream);
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
            } catch (ContentReadException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(input);
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