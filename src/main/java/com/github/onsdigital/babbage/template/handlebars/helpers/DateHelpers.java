package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.github.onsdigital.babbage.configuration.AppConfiguration.appConfig;

/**
 * Created by bren on 10/06/15.
 */
public enum DateHelpers implements BabbageHandlebarsHelper<String> {

    /**
     *
     * Formats given input date string to output date string.
     *
     * Patterns must be java date patterns
     *
     * {{df dateString [inputFormat=pattern] [outputFormat]}}
     */
    df {
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
                System.out.println("Failed formating date : " + date);
                return "";
            }
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    },

    last24hours {
        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }

        @Override
        public CharSequence apply(String date, Options options) throws IOException {
            if (options.isFalsy(date)) {
                return null;
            }

            try {
                Date parsedDate = new SimpleDateFormat(resolveInputFormat(options)).parse(date.toString());
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY, -24);
                Date dayBefore = calendar.getTime();
                if (parsedDate.after(dayBefore)) {
                    return _true();
                } else {
                    return null;
                }

            } catch (Exception e) {
                System.out.println("Failed formatting date : " + date);
                return null;
            }

        }

        private String _true() {
            return String.valueOf(Boolean.TRUE);
        }
    }
    ;

    private static final TimeZone timeZone = TimeZone.getTimeZone("Europe/London");

    private static String resolvePattern(Options options) {
        return options.hash("outputFormat", Configuration.HANDLEBARS.getHandlebarsDatePattern());
    }

    private static String resolveInputFormat(Options options) {
        return  options.hash("inputFormat", appConfig().contentAPI().defaultContentDatePattern());
    }

}
