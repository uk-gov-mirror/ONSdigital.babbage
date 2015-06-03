package com.github.onsdigital.request.handler;

import com.github.onsdigital.data.DataNotFoundException;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.data.zebedee.ZebedeeClient;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.handler.base.RequestHandler;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Handle data requests. Diverts data requests to Zebedee if Florence is logged on on client machine
 */
public class DataRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "data";

    @Override
    public Object handle(String requestedUri, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Cookie[] cookies = request.getCookies();
        boolean handled = handleZebedeeRequest(requestedUri, cookies, response);
        if (handled) {
            return null;//done
        }

        //Read from Babbage if not Zebedee request
        IOUtils.copy(DataService.getInstance().getDataStream(requestedUri), response.getOutputStream());
        configureResponse(response);
        return null;

    }


    /**
     * @param requestedUri
     * @param request
     * @return Requested data as string, either from Zebedee if authenticated or from Babbage DataService
     */
    public String getDataAsString(String requestedUri, HttpServletRequest request) throws IOException {
        ZebedeeRequest zebedeeRequest = getZebedeeRequest(requestedUri, request.getCookies());
        if (zebedeeRequest != null) {
            ZebedeeClient zebedeeClient = new ZebedeeClient();
            try {
                InputStream dataStream = getDataStream(zebedeeClient, zebedeeRequest);
                if (dataStream != null) {
                    try (InputStreamReader reader = new InputStreamReader(dataStream)) {
                        return IOUtils.toString(reader);
                    }
                } else {
                    throw new DataNotFoundException(requestedUri);
                }
            } finally {
                zebedeeClient.closeConnection();
            }
        }

        //Return from Babbage local fs if no a Zebedee request
        return DataService.getInstance().getDataAsString(requestedUri,false);
    }

    private void configureResponse(HttpServletResponse response) {
        // Add a five-minute cache time to static files to reduce round-trips to
        // the server and increase performance whilst still allowing the system
        // to be updated quite promptly if necessary:
//        if (!HostHelper.isLocalhost(request)) {
//            response.addHeader("cache-control", "public, max-age=300");
//        }
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");
    }

    /**
     * Delegates request to zebedee client if this is a zebedee request
     *
     * @param uri
     * @param cookies
     * @return returns true if request is handled, false otherwise
     */
    private boolean handleZebedeeRequest(String uri, Cookie[] cookies, HttpServletResponse response) throws IOException {

        ZebedeeRequest zebedeeRequest = getZebedeeRequest(uri, cookies);
        if (zebedeeRequest != null) {
            return readFromZebedee(zebedeeRequest, response);
        }

        return false;
    }


    //Read stream from zebedee and copy to response
    private boolean readFromZebedee(ZebedeeRequest request, HttpServletResponse response) throws IOException {

        InputStream dataStream = null;
        ZebedeeClient zebedeeClient = new ZebedeeClient();
        try {
            dataStream = getDataStream(zebedeeClient, request);
            //If not read from Zebedee try babbage local file system
            if (dataStream != null) {
                IOUtils.copy(dataStream, response.getOutputStream());
                configureResponse(response);
                return true;
            }
            return false;
        } finally {
            zebedeeClient.closeConnection();
        }

    }

    //Read from Zebedee
    private InputStream getDataStream(ZebedeeClient zebedeeClient, ZebedeeRequest zebedeeRequest) throws IOException {
        return zebedeeClient.startDataStream(zebedeeRequest);
    }

    private ZebedeeRequest getZebedeeRequest(String uri, Cookie[] cookies) {

        if (cookies == null) {
            return null;
        }

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
