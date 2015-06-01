package com.github.onsdigital.request.handler;

import com.github.onsdigital.data.DataService;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.zebedee.ZebedeeClient;
import com.github.onsdigital.zebedee.ZebedeeRequest;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bren on 28/05/15.
 */
public class DataRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "data";

    @Override
    public Object handle(String requestedUri, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Cookie[] cookies = request.getCookies();
        ZebedeeClient zebedeeClient = new ZebedeeClient();
        try {
            InputStream dataStream = handleZebedeeRequest(zebedeeClient, requestedUri, cookies);

            //If not read from Zebedee try babbage local file system
            if (dataStream != null) {
                IOUtils.copy(dataStream, response.getOutputStream());
            }

            // Add a five-minute cache time to static files to reduce round-trips to
            // the server and increase performance whilst still allowing the system
            // to be updated quite promptly if necessary:
//        if (!HostHelper.isLocalhost(request)) {
//            response.addHeader("cache-control", "public, max-age=300");
//        }
        } finally {
            zebedeeClient.closeConnection();
        }

        System.out.println("Zebedee could not find any data. Trying Babbage file system");
        dataStream = DataService.getInstance().getDataStream(requestedUri);

        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");
        return null;

    }

    /**
     * Delegates request to zebedee client if this is a zebedee request
     *
     * @param uri
     * @param cookies
     * @return returns true if request is handled, false otherwise
     */
    private InputStream handleZebedeeRequest(ZebedeeClient client, String uri, Cookie[] cookies) throws IOException {
        if (cookies != null) {
            ZebedeeRequest zebedeeRequest = getZebedeeRequest(uri, cookies);
            if (zebedeeRequest != null) {
                return client.startDataStream(zebedeeRequest);
            }
        }
        return null;
    }

    private ZebedeeRequest getZebedeeRequest(String uri, Cookie[] cookies) {

        String collection = null;
        String accessToken = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("collection")) {
                System.out.println("Found collection cookie: " + cookie.getValue());
                collection = cookie.getValue();
            }
            if (cookie.getName().equals("access_token")) {
                System.out.println("Found access_token cookie: " + cookie.getValue());
                accessToken = cookie.getValue();
            }
        }

        if (collection != null) {
            return new ZebedeeRequest(uri, collection, accessToken);
        }

        return null; //No collection token found
    }


    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
