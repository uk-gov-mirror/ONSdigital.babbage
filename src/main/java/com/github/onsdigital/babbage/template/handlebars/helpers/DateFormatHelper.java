package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.configuration.Configuration;
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
            options.inverse();
        }
        try {
            Date parseDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(date);
            String pattern = resolvePattern(options.params);
            return FastDateFormat.getInstance(pattern, timeZone).format(parseDate);
        } catch (Exception e) {
            System.out.println("Failed fomatting date : " + date);
            e.printStackTrace();
            return options.inverse();
        }
    }

    private String resolvePattern(Object[] params) {
        if (params == null || params.length == 0) {
            return Configuration.HANDLEBARS.getHandlebarsDatePattern();
        }

        return (String) params[0];
    }

    @Override
    public String getHelperName() {
        return HELPER_NAME;
    }
}
