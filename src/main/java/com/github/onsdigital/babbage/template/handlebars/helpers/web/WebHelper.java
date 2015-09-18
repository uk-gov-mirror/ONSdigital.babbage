package com.github.onsdigital.babbage.template.handlebars.helpers.web;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNoneEmpty;

/**
 * Created by bren on 18/09/15.
 */
public enum WebHelper implements BabbageHandlebarsHelper<Object> {

    query_string {
        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            Map<String,Object> queryParameters = (Map<String, Object>) options.context.get("parameters");
            String exclude = options.hash("exclude");

            StringBuilder builder = new StringBuilder();
            Iterator<Map.Entry<String, Object>> iterator = queryParameters.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry next =  iterator.next();
                String key = (String) next.getKey();
                Object[] values = (Object[]) next.getValue();
                for (Object o : values) {
                    if (key.equals(exclude)) {
                        continue;
                    }
                    builder.append(key).append("=").append(o);
                    if (iterator.hasNext()) {
                        builder.append("&");
                    }
                }
            }
            String queryString = builder.toString();
            queryString = isNoneEmpty(queryString) ? "?" + queryString : queryString;
            return new Handlebars.SafeString(queryString);
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    }

}
