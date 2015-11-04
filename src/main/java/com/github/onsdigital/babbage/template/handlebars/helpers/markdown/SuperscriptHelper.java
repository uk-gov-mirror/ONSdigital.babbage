package com.github.onsdigital.babbage.template.handlebars.helpers.markdown;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;

import java.io.IOException;

/**
 * Created by bren on 03/11/15.
 */
public class SuperscriptHelper implements BabbageHandlebarsHelper<String> {
    private final static String SUPER_SCRIPT_PATTERN = "\\^(?=\\S)(\\S*)\\^";

    @Override
    public void register(Handlebars handlebars) {
        handlebars.registerHelper("superScript", this);
    }

    @Override
    public CharSequence apply(String text, Options options) throws IOException {
        if (options.isFalsy(text)) {
            return "";
        }
        return doSuperscript(text);
    }

    static String doSuperscript(String text) {
        return text.toString().replaceAll(SUPER_SCRIPT_PATTERN, "<sup>$1</sup>");
    }
}
