package com.github.onsdigital.babbage.api.util;

import com.github.onsdigital.babbage.util.json.JsonUtil;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by bren on 18/01/16.
 */
public class ResponseUtils {

    public static void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON);
        IOUtils.write(JsonUtil.toJson(data), response.getOutputStream());
    }

    public static void sendHtmlResponse(HttpServletResponse response, String html) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.TEXT_HTML);
        IOUtils.write(html, response.getOutputStream());
    }
}
