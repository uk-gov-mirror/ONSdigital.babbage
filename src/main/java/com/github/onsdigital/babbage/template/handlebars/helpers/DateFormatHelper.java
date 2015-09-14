package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by bren on 10/06/15.
 * <p>
 * //Date format helper
 */
public class DateFormatHelper implements BabbageHandlebarsHelper<String> {

    private final String HELPER_NAME = "df";
    private static final TimeZone timeZone = TimeZone.getTimeZone("Europe/London");

    @Override
    public CharSequence apply(String date, Options options) throws IOException {
        if (options.isFalsy(date)) {
            return "";
        }
        try {
            Date parsedDate = new SimpleDateFormat(resolveInputFormat(options)).parse(date.toString());
            String pattern = resolvePattern(options);
            return FastDateFormat.getInstance(pattern, timeZone).format(parsedDate);
        } catch (Exception e) {
            System.out.println("Failed fomatting date : " + date);
            return "";
        }
    }

    private String resolvePattern(Options options) {
        return options.hash("outputFormat", Configuration.HANDLEBARS.getHandlebarsDatePattern());
    }

    private String resolveInputFormat(Options options) {
     return  options.hash("inputFormat",Configuration.CONTENT_SERVICE.getDefaultContentDatePattern());
    }

    @Override
    public void register(Handlebars handlebars) {
        handlebars.registerHelper(HELPER_NAME, this);
    }
}
