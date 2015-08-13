package com.github.onsdigital.babbage.template.handlebars.helpers.resolve;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.util.URIUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.babbage.util.JsonUtil.deserialiseArray;
import static com.github.onsdigital.babbage.util.JsonUtil.deserialiseObject;

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
            try {
                validateUri(uri);
                String filter = options.hash("filter");
                Map<String, String[]> parameters = new HashMap<>();
                if (filter != null) {
                    parameters.put(filter, null);
                }
                InputStream data = ContentClient.getInstance().getContentStream(uri, parameters).getDataStream();
                Map<String, Object> context = deserialiseObject(data);
                assign(options, context);
                return options.fn(context);
            } catch (ContentReadException e) {
                logResolveError(uri, e);
                return options.inverse();
            } catch (IllegalArgumentException e) {
                logResolveError(uri, e);
                return options.inverse();
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
            try {
                validateUri(uri);
                InputStream data = ContentClient.getInstance().getChildren(uri, getHashParameters(options, "depth")).getDataStream();
                List<Map<String, Object>> context = deserialiseArray(data);
                assign(options, context);
                return options.fn(context);
            } catch (ContentReadException e) {
                logResolveError(uri, e);
                return options.inverse();
            } catch (IllegalArgumentException e) {
                logResolveError(uri, e);
                return options.inverse();
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
            try {
                validateUri(uri);
                InputStream data = ContentClient.getInstance().getParents(uri).getDataStream();
                List<Map<String, Object>> context = deserialiseArray(data);
                assign(options, context);
                return options.fn(context);
            } catch (ContentReadException e) {
                logResolveError(uri, e);
                return options.inverse();
            } catch (IllegalArgumentException e) {
                logResolveError(uri, e);
                return options.inverse();
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


    /**
     * Gets hash parameters with given names and puts them into  map format required by ContentClient
     *
     * @param options
     * @param names
     * @return
     */
    private static Map<String, String[]> getHashParameters(Options options, String... names) {
        Map<String, String[]> parameterMap = new HashMap<>();
        for (String name : names) {
            Object value = options.hash(name);
            if (value != null) {
                parameterMap.put(name, new String[]{String.valueOf(value)});
            }
        }
        return parameterMap;
    }

    private static void logResolveError(String uri, Exception e) {
        System.err.printf("Failed resolving data, uri: %s cause: %s", uri, e.getMessage());

    }

}
