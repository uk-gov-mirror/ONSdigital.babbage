package com.github.onsdigital.api.data;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.api.handler.DataRequestHandler;
import com.github.onsdigital.api.util.ApiErrorHandler;
import com.github.onsdigital.api.util.URIUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;

@Api
public class Data {

    @GET
    public Object getData(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {


        try {
            new DataRequestHandler().handleDataRequest(URIUtils.extractUri(request.getRequestURI()), response);
            return "";
        } catch (Exception e) {
            return ApiErrorHandler.handle(e, response);
        }

        // Add a five-minute cache time to static files to reduce round-trips to
        // the server and increase performance whilst still allowing the system
        // to be updated quite promptly if necessary:
//        if (!HostHelper.isLocalhost(request)) {
//            response.addHeader("cache-control", "public, max-age=300");
//        }

//        String collection = "";
//        String authenticationToken = "";
//        final String authenticationHeader = "X-Florence-Token";
//
//        if (request.getCookies() != null) {
//            for (Cookie cookie : request.getCookies()) {
//                if (cookie.getName().equals("collection")) {
//                    System.out.println("Found collection cookie: " + cookie.getValue());
//                    collection = cookie.getValue();
//                }
//                if (cookie.getName().equals("access_token")) {
//                    System.out.println("Found access_token cookie: " + cookie.getValue());
//                    authenticationToken = cookie.getValue();
//                }
//            }
//        }
//
//        InputStream data = null;
//        if (StringUtils.isEmpty(collection)) {
//            data = DataService.getInstance().getDataStream(request.getRequestURI());
//        } else {
//            URI uri = URI.create(request.getRequestURI());
//            String uriPath = DataService.getInstance().cleanPath(uri);
//
//            if (uriPath.length() > 0) {
//                uriPath += "/";
//            }
//
//            uriPath += "data.json";
//
//            try {
//                String url = Configuration.getZebedeeUrl() + "/content/" + collection;
//
//                System.out.println("Calling zebedee: " + url + " for path " + uriPath + " with token: " + authenticationToken);
//
//                HttpGet httpGet = new HttpGet(Configuration.getZebedeeUrl() + "/content/" + collection + "?uri=" + uriPath);
//                httpGet.addHeader(authenticationHeader, authenticationToken);
//
//                CloseableHttpClient httpClient = HttpClients.createDefault();
//                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
//
//                try {
//                    HttpEntity responseEntity = httpResponse.getEntity();
//
//                    //String responseText = "";
//
//                    if (responseEntity != null && responseEntity.getContent() != null) {
//                        //responseText = IOUtils.toString(responseEntity.getContent());
//                        //data = IOUtils.toInputStream(responseText);
//                        data = responseEntity.getContent();
//                    }
//
//                    System.out.println("Response: " + httpResponse.getStatusLine());
//
//                    return processResponse(response, data);
//                    //EntityUtils.consume(responseEntity);
//
//                } catch (IOException e) {
//                    System.out.println("IOException " + e.getMessage());
//                    e.printStackTrace();
//                } finally {
//                    httpResponse.close();
//                }
//
//            } catch (Exception e) {
//                // Look for a data file:
//                System.out.println("Exception calling zebedee: " + e.getMessage());
//                e.printStackTrace();
//                data = DataService.getInstance().getDataStream(request.getRequestURI());
//            }
//        }
//        return processResponse(response, data);


    }

}
