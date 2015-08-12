package com.github.onsdigital.babbage.template.handlebars.helpers.resolve;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.util.URIUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bren on 11/08/15.
 */
public enum DataHelpers implements BabbageHandlebarsHelper<String> {

    /**
     * usage: {{#resolve "variableName" "uri" ["filter"]}}
     * <p/>
     * If variableName is not empty data is assigned to given variable name
     */
    resolve {
        @Override
        public String getHelperName() {
            return "resolve";
        }

        @Override
        public CharSequence apply(String name, Options options) throws IOException {
            try {
                String filter = options.param(1, null);
                Map<String, String[]> parameterMap = null;
                if (filter != null) {
                    parameterMap = new HashMap<>();
                    parameterMap.put(filter, null);
                }
                String data = ContentClient.getInstance().getContentStream(getUri(options), parameterMap).getAsString();
                return options.fn(toJsonNode(name, data));
            } catch (ContentReadException e) {
                e.printStackTrace();
                return options.inverse();
            }
        }
    },

    /**
     * usage:  {{#resolveChildren "variableName" "uri" ["depth"]}}
     * <p/>
     * If variableName is not empty data is assigned to given variable name
     */
    resolveChildren {
        @Override
        public String getHelperName() {
            return "resolveChildren";
        }

        @Override
        public CharSequence apply(String name, Options options) throws IOException {
            try {
                Integer depth = options.param(1, 1);
                Map<String, String[]> parameterMap = new HashMap<>();
                ;
                parameterMap.put("depth", new String[]{String.valueOf(depth)});
                String data = ContentClient.getInstance().getChildren(getUri(options), parameterMap).getAsString();
                return options.fn(toJsonNode(name, data));
            } catch (ContentReadException e) {
                e.printStackTrace();
                return options.inverse();
            }
        }
    },

    /**
     * usage:  {{#resolveParents "variableName" "uri"}}
     * <p/>
     * If variableName is not empty data is assigned to given variable name
     */
    resolveParents {
        @Override
        public String getHelperName() {
            return "resolveParents";
        }

        @Override
        public CharSequence apply(String name, Options options) throws IOException {
            try {
                String data = ContentClient.getInstance().getParents(getUri(options)).getAsString();
                return options.fn(toJsonNode(name, data));
            } catch (ContentReadException e) {
                e.printStackTrace();
                return options.inverse();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return options.inverse();
            }
        }
    };

    //gets first parameter as uri, throws exception if not valid
    private static String getUri(Options options) throws IOException {
        if (options.params == null || options.params.length < 1) {
            throw new IllegalArgumentException("Data Helpers: No uri given for resolving");
        }
        try {
            String uri = String.valueOf(options.params[0]);
//            URIUtil.validate(uri);
            return uri;
        } catch (URIUtil.InvalidUriException e) {
            throw new IllegalArgumentException("Data Helpers: Invalid uri, can not resolve");
        }


    }

    private static JsonNode toJsonNode(String name, String data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        if (StringUtils.isEmpty(name)) {
            return mapper.readValue(String.valueOf(data), JsonNode.class);
        } else {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put(name, mapper.readValue(String.valueOf(data), JsonNode.class));
            return objectNode;
        }
    }

}
