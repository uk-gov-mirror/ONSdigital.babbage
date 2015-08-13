package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.util.HelperUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;

/**
 * Created by bren on 08/07/15.
 */
public enum FileHelpers implements BabbageHandlebarsHelper<String> {

    /*File size helper*/
    fs {

        @Override
        public CharSequence apply(String uri, Options options) throws IOException {
            if (options.isFalsy(uri)) {
                return null;
            }
            try {
                Long size = HelperUtils.getFileSize(uri);
                return humanReadableByteCount(size, true);
            } catch (IOException e) {
                return null;
            }
        }

        // Taken from http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
        private String humanReadableByteCount(long bytes, boolean si) {
            int unit = si ? 1000 : 1024;
            if (bytes < unit) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(unit));
            String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
            return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }

    },

    /*File extension*/
    fe {

        @Override
        public CharSequence apply(String uri, Options options) throws IOException {
            if (options.isFalsy(uri)) {
                return null;
            }
            return FilenameUtils.getExtension(uri);
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }

    }
}
