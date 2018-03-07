package com.github.onsdigital.babbage.template.handlebars.helpers.markdown;

import com.github.jknack.handlebars.Handlebars;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util.ChartTagReplacer;
import com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util.ImageTagReplacer;
import com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util.InteractiveTagReplacer;
import com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util.MathjaxTagReplacer;
import com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util.TableTagReplacer;
import com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util.TableTagV2Replacer;
import com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util.PulloutBoxTagReplacer;

import java.io.IOException;

/**
 * Created by bren on 28/07/15.
 * <p/>
 * Extending functionality of Handlebars java markdown helper
 */
public class PdfMarkdownHelper extends CustomMarkdownHelper implements BabbageHandlebarsHelper<Object> {

    private final String HELPER_NAME = "pdf-md";

    @Override
    public void register(Handlebars handlebars) {
        handlebars.registerHelper(HELPER_NAME, this);
    }

    @Override
    protected String processCustomMarkdownTags(String path, String markdown) throws IOException {
        markdown = new ChartTagReplacer(path, "pdf/partials/chart").replaceCustomTags(markdown);
        markdown = new MathjaxTagReplacer(path, "pdf/partials/equation").replaceCustomTags(markdown);
        markdown = new TableTagReplacer(path, "pdf/partials/table").replaceCustomTags(markdown);
        markdown = new TableTagV2Replacer(path, "pdf/partials/table-v2").replaceCustomTags(markdown);
        markdown = new ImageTagReplacer(path, "pdf/partials/image").replaceCustomTags(markdown);
        markdown = new InteractiveTagReplacer(path, "pdf/partials/interactive").replaceCustomTags(markdown);
        markdown = new PulloutBoxTagReplacer(path, "pdf/partials/pullout-box").replaceCustomTags(markdown);
        return markdown;
    }
}
