package com.github.onsdigital.babbage.template.handlebars.helpers.resolve;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bren on 11/08/15.
 */
public enum DataHelpers implements BabbageHandlebarsHelper<String> {

    resolve {
        @Override
        public String getHelperName() {
            return "resolve";
        }

        @Override
        public CharSequence apply(String uri, Options options) throws IOException {
            if (options.isFalsy(uri)) {
                return options.inverse();
            } else {
                try {
                    String data = ContentClient.getInstance().getContentStream(uri, getParameters(options.params, options)).getAsString();
                    return options.fn(toJsonNode(data));
                } catch (ContentReadException e) {
                    e.printStackTrace();
                    return options.inverse();
                }
            }
        }
    },


    resolveChildren {
        @Override
        public String getHelperName() {
            return "resolveChildren";
        }

        @Override
        public CharSequence apply(String uri, Options options) throws IOException {
            if (options.isFalsy(uri)) {
                return options.inverse();
            } else {
                try {
                    String data = ContentClient.getInstance().getChildren(uri, getParameters(options.params, options)).getAsString();
                    return options.fn(toJsonNode(data));
                } catch (ContentReadException e) {
                    e.printStackTrace();
                    return options.inverse();
                }
            }
        }
    },

    resolveParents {
        @Override
        public String getHelperName() {
            return "resolveParents";
        }

        @Override
        public CharSequence apply(String uri, Options options) throws IOException {
            if (options.isFalsy(uri)) {
                return options.inverse();
            } else {
                try {
                    String data = ContentClient.getInstance().getParents(uri, getParameters(options.params, options)).getAsString();
                    return options.fn(toJsonNode(data));
                } catch (ContentReadException e) {
                    e.printStackTrace();
                    return options.inverse();
                }
            }
        }
    };


    private static JsonNode toJsonNode(String data) throws IOException {
        return new ObjectMapper().readValue(String.valueOf(data), JsonNode.class);
    }

    private static Map<String,String[]> getParameters(Object[] parameters, Options options) {
        Map<String, String[]> queryParameters = new HashMap<>();
        for (Object parameter : parameters) {
            if (options.isFalsy(parameter)) {
                continue;
            }
            queryParameters.put(String.valueOf(parameter), null);
        }

        return queryParameters;
    }


}
