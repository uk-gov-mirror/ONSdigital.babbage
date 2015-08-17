package com.github.onsdigital.babbage.template.handlebars.helpers.resolve;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.util.URIUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.babbage.content.client.ContentClient.depth;
import static com.github.onsdigital.babbage.content.client.ContentClient.filter;
import static com.github.onsdigital.babbage.util.json.JsonUtil.toList;
import static com.github.onsdigital.babbage.util.json.JsonUtil.toMap;

/**
 * Created by bren on 11/08/15.
 */
public enum DataHelpers implements BabbageHandlebarsHelper<String> {

    /**
     * usage: {{#resolve "uri" [filter=]}}
     * <p>
     * If variableName is not empty data is assigned to given variable name
     */
    resolve {
        @Override
        public CharSequence apply(String uri, Options options) throws IOException {
            ContentStream contentStream = null;
            try {
                validateUri(uri);

                ContentFilter filter = null;
                String filterVal = options.<String>hash("filter");
                if (filterVal != null) {
                    filter = ContentFilter.valueOf(filterVal.toUpperCase());
                }
                contentStream = ContentClient.getInstance().getContentStream(uri, filter(filter));
                InputStream data = contentStream.getDataStream();
                Map<String, Object> context = toMap(data);
                assign(options, context);
                return options.fn(context);
            } catch (Exception e) {
                logResolveError(uri, e);
                return options.inverse();
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }

    },

    //Resolve latest article or bulletin with given uri
    resolveLatest {
        @Override
        public CharSequence apply(String uri, Options options) throws IOException {
            ContentStream contentStream = null;
            try {
                validateUri(uri);
                String s = URIUtil.removeLastSegment(uri) + "/latest";

                ContentFilter filter = null;
                String filterVal = options.<String>hash("filter");
                if (filterVal != null) {
                    filter = ContentFilter.valueOf(filterVal.toUpperCase());
                }
                contentStream = ContentClient.getInstance().getContentStream(s, filter(filter));
                InputStream data = contentStream.getDataStream();
                Map<String, Object> context = toMap(data);
                assign(options, context);
                return options.fn(context);
            } catch (Exception e) {
                logResolveError(uri, e);
                return options.inverse();
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }

        }


        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    },

    /**
     * usage:  {{#resolveChildren "uri" [depth=depthvalue] [assign=variableName]}
     * <p>
     * If assign is not empty data is assigned to given variable name
     */
    resolveChildren {
        @Override
        public CharSequence apply(String uri, Options options) throws IOException {
            ContentStream stream = null;
            try {
                validateUri(uri);
                Integer depth = options.<Integer>hash("depth");
                stream = ContentClient.getInstance().getChildren(uri, depth(depth));
                InputStream data = stream.getDataStream();
                List<Map<String, Object>> context = toList(data);
                assign(options, context);
                return options.fn(context);
            } catch (Exception e) {
                logResolveError(uri, e);
                return options.inverse();
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }

    },

    /**
     * usage:  {{#resolveParents "variableName" "uri"}}
     * <p>
     * If variableName is not empty data is assigned to given variable name
     */
    resolveParents {
        @Override
        public CharSequence apply(String uri, Options options) throws IOException {
            ContentStream stream = null;
            try {
                validateUri(uri);
                stream = ContentClient.getInstance().getParents(uri);
                InputStream data = stream.getDataStream();
                List<Map<String, Object>> context = toList(data);
                assign(options, context);
                return options.fn(context);
            } catch (Exception e) {
                logResolveError(uri, e);
                return options.inverse();
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }

    };

    //gets first parameter as uri, throws exception if not valid
    private static void validateUri(String uri) throws IOException {
        if (uri == null) {
            throw new IllegalArgumentException("Data Helpers: No uri given for resolving");
        }
        try {
            URIUtil.validate(uri);
        } catch (URIUtil.InvalidUriException e) {
            throw new IllegalArgumentException("Invalid uri, can not resolve");
        }


    }


    /**
     * Assigns data to current context if assign parameter given
     *
     * @param options
     * @param data
     */
    private static void assign(Options options, Object data) {
        String variableName = options.hash("assign");
        if (StringUtils.isNotEmpty(variableName)) {
            options.context.data(variableName, data);
        }
    }

    private static void logResolveError(String uri, Exception e) {
        System.err.printf("Failed resolving data, uri: %s cause: %s", uri, e.getMessage());

    }

}
