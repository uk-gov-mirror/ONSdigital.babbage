package com.github.onsdigital.babbage.template.handlebars.helpers.markdown;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.MarkdownHelper;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util.*;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.github.onsdigital.babbage.util.ThreadContext;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Created by bren on 28/07/15.
 * <p/>
 * Extending functionality of Handlebars java markdown helper
 */
public class CustomMarkdownHelper extends MarkdownHelper implements BabbageHandlebarsHelper<Object> {

    private final String HELPER_NAME = "md";

    @Override
    public CharSequence apply(Object context, Options options) throws IOException {
        if (options.isFalsy(context)) {
            return "";
        }

        String path;
        try {
            path = ((LinkedHashMap<String, Object>)options.context.parent().model()).get("uri").toString();
        } catch (Exception e) {
            RequestUtil.Location location = (RequestUtil.Location)ThreadContext.getData("location");
            path = location.getPathname();
        }

        String markdown = context.toString();

        // Extensions are defined via a bitmask passed into the pegdown constructor.
        // To enable further extensions just add the value to the extensions variable.
        int extensions = Extensions.TABLES;
        PegDownProcessor processor = new PegDownProcessor(extensions);
        markdown = processor.markdownToHtml(markdown);

        markdown = SubscriptHelper.doSubscript(markdown);
        markdown = SuperscriptHelper.doSuperscript(markdown);
        markdown = new ChartTagReplacer(path).replaceCustomTags(markdown);
        markdown = new TableTagReplacer(path).replaceCustomTags(markdown);
        markdown = new ImageTagReplacer(path).replaceCustomTags(markdown);
        markdown = new InteractiveTagReplacer(path).replaceCustomTags(markdown);
        markdown = MathjaxRenderer.render(markdown);
        return new Handlebars.SafeString(markdown) ;
    }

    @Override
    public void register(Handlebars handlebars) {
        handlebars.registerHelper(HELPER_NAME, this);
    }
}
