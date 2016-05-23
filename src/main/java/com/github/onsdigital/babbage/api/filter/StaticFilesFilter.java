package com.github.onsdigital.babbage.api.filter;

import com.github.davidcarboni.restolino.framework.Filter;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.response.BabbageContentBasedBinaryResponse;
import com.github.onsdigital.babbage.util.URIUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.github.onsdigital.babbage.api.error.ErrorHandler.handle;

public class StaticFilesFilter implements Filter {

    private static final Type type = new TypeToken<Map<String, String>>(){}.getType();
    private static final String visualisationRoot = "visualisations";

    @Override
    public boolean filter(HttpServletRequest request, HttpServletResponse response) {

        try {
            String uri = request.getRequestURI();
            Path requestPath = Paths.get(uri);

            if (requestPath.getNameCount() < 2) // requires two parts to the path to be a valid visualisation: /visualisation/code/
                return true; // there is no path, do not try and handle it.
            

            Path endpoint = requestPath.getName(0);
            if (endpoint.toString().equalsIgnoreCase("visualisations")) {

                Path uid = Paths.get(uri).getName(1);
                String path = requestPath.getFileName().toString();

                if (path.length() == 0 || path.equals("/")) {
                    String jsonPath = String.format("/%s/%s", visualisationRoot, uid);
                    ContentResponse contentResponse = ContentClient.getInstance().getContent(jsonPath);

                    JsonParser parser = new JsonParser();
                    JsonObject obj = parser.parse(contentResponse.getAsString()).getAsJsonObject();
                    JsonElement indexPage = obj.get("indexPage");
                    path = indexPage == null ? "" : indexPage.getAsString();

                    if (path == null || path.length() == 0 || path.equals("/")) {
                        path = "index.html";
                    }
                }

                String visualisationPath = String.format("/%s/%s/content/%s", visualisationRoot, uid, path);

                ContentResponse contentResponse = ContentClient.getInstance().getResource(visualisationPath);
                new BabbageContentBasedBinaryResponse(contentResponse, contentResponse.getDataStream(), contentResponse.getMimeType()).apply(request, response);

                return false; // we have the response we require, do not continue to process this request.
            }
        } catch (Throwable t) {
            try {
                handle(request, response, t);
            } catch (IOException e) {
                return true;
            }
        }

        return true; // continue onto other filters / handlers
    }

    public static void main(String[] args) {
        String path = "/visualisation";

        System.out.println(Paths.get(path).getNameCount());


        Path endpoint = Paths.get(path).getName(0);
        Path uid = Paths.get(path).getName(1);



        boolean endpointIsVis = endpoint.toString().equalsIgnoreCase("visualisation");

        System.out.println("endpointIsVis = " + endpointIsVis);


        System.out.println("uid = " + uid);
        String removeEndpoint = URIUtil.removeEndpoint(URIUtil.removeEndpoint(path));

        System.out.println("removeEndpoint = " + removeEndpoint);
    }
}