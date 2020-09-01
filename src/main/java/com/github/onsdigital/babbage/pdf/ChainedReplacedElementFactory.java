package com.github.onsdigital.babbage.pdf;

import com.openhtmltopdf.extend.ReplacedElement;
import com.openhtmltopdf.extend.ReplacedElementFactory;
import com.openhtmltopdf.extend.UserAgentCallback;
import com.openhtmltopdf.layout.LayoutContext;
import com.openhtmltopdf.render.BlockBox;
import com.openhtmltopdf.layout.SharedContext;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

/**
 * 
 */
public class ChainedReplacedElementFactory implements ReplacedElementFactory {
    private List factoryList;
    private ReplacedElementFactory defaultReplacedElementFactory;

    public ChainedReplacedElementFactory(SharedContext ctx) {
        this.factoryList = new ArrayList();
        this.defaultReplacedElementFactory = ctx.getReplacedElementFactory();
    }

    @Override
    public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
        for (Iterator it = factoryList.iterator(); it.hasNext();) {
            ReplacedElementFactory ref = (ReplacedElementFactory) it.next();
            ReplacedElement re = ref.createReplacedElement(c, box, uac, cssWidth, cssHeight);
            if (re != null) {
                return re;
            }
        }
        return this.defaultReplacedElementFactory.createReplacedElement(c, box, uac, cssWidth, cssHeight);
    }

    public void addFactory(ReplacedElementFactory ref) {
        this.factoryList.add(ref);
    }

    @Override
    public boolean isReplacedElement(Element e) {
        for (Iterator it = factoryList.iterator(); it.hasNext();) {
            ReplacedElementFactory ref = (ReplacedElementFactory) it.next();
            boolean isReplaced = ref.isReplacedElement(e);
            if (isReplaced != false) {
                return isReplaced;
            }
        }
        return this.defaultReplacedElementFactory.isReplacedElement(e);
    }
}