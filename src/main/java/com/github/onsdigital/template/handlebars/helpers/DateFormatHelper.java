package com.github.onsdigital.template.handlebars.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.configuration.Configuration;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by bren on 10/06/15.
 * <p>
 * //Date format helper
 */
public class DateFormatHelper implements Helper<Date> {

    private static final TimeZone timeZone = TimeZone.getTimeZone("Europe/London");

    @Override
    public CharSequence apply(Date date, Options options) throws IOException {
        if (date == null) {
            return null;
        }
        try {
            String pattern = resolvePattern(options.params);
            return FastDateFormat.getInstance(pattern, timeZone).format(date);
        } catch (Exception e) {
            System.out.println("Failed fomatting date : " + date);
            e.printStackTrace();
            return "Invalid date?";
        }
    }

    private String resolvePattern(Object[] params) {
        if (params == null || params.length == 0) {
            return Configuration.HANDLEBARS.getHandlebarsDatePattern();
        }

        return (String) params[0];
    }

}
