package com.github.onsdigital.search;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.base.PageType;
import com.google.gson.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Helper methods for loading index into search engine
 */
public class LoadIndexHelper {
    private static final String DESCRIPTION = "description";
    private static final String CDID = "cdid";
    private static final String TAGS = "tags";
    private static final String TITLE = "title";
    private static final String TYPE = "type";
    private static final String URI = "uri";
    private static final String DELIMITTER = "/";
    private static final String SUMMARY = "summary";

    /**
     * Loads up the file names from a system scan
     *
     * @return list of strings representing files
     * @throws IOException if any file io operations failed
     */
    public static List<String> getAbsoluteFilePaths(String path) throws IOException {
        List<String> fileNames = new ArrayList<String>();
        final Path rootDir = Paths.get(path);
        fileNames = ScanFileSystem.getFileNames(fileNames, rootDir);
        return fileNames;
    }

    /**
     * Builds up a map that represents the data structure for indexing
     *
     * @param absoluteFilePath the complete path and filename
     * @return the collection of key value pairs representing an indexable item
     * @throws IOException
     * @throws JsonSyntaxException
     * @throws JsonIOException
     */
    public static Map<String, String> getDocumentMap(String absoluteFilePath) throws JsonIOException, JsonSyntaxException, IOException {
        String url = absoluteFilePath.substring(absoluteFilePath.indexOf(Configuration.getContentPath()) + Configuration.getContentPath().length());
        String[] splitPath = url.split(DELIMITTER);

        List<String> splitPathAsList = new ArrayList<String>(Arrays.asList(splitPath));
        // remove first index which is just a space
        splitPathAsList.remove(0);
        // remove last index which is the data.json
        splitPathAsList.remove(splitPathAsList.size() - 1);

        JsonObject jsonObject = getJsonObject(absoluteFilePath);
        String type = getField(jsonObject, TYPE);

        Map<String, String> documentMap = null;
        PageType pageType = PageType.valueOf(type);
        String splitUrl = url.substring(0, url.indexOf("data.json"));
        JsonObject description = jsonObject.getAsJsonObject(DESCRIPTION);
        String title = getField(description, TITLE);
        String summary = getField(description, SUMMARY);
//        String name = getField(jsonObject, NAME);
//        String lede = getField(jsonObject, LEDE);
        switch (pageType) {
            case taxonomy_landing_page:
            case product_page:
                documentMap = buildDocumentMap(splitUrl, splitPathAsList, type, title, summary);
                break;
            case timeseries:
                String cdid = getField(description, CDID);
                documentMap = buildTimeseriesMap(splitUrl, splitPathAsList, type, title, cdid);
                break;
            case unknown:
                System.out.println("json file: " + absoluteFilePath + "has unknown content type: " + pageType);
                break;
            default:
                documentMap = buildDocumentMap(splitUrl, splitPathAsList, type, title, summary);
                break;
        }

        return documentMap;
    }

    private static Map<String, String> buildDocumentMap(String url, List<String> pathTokens, String type, String title, String summary) {

        Map<String, String> documentMap = new HashMap<String, String>();
        documentMap.put(URI, url);
        documentMap.put(TYPE, type);
        documentMap.put(TITLE, title);
        documentMap.put(TAGS, pathTokens.toString());
//        documentMap.put(LEDE, lede);
        documentMap.put(SUMMARY, summary);
        return documentMap;
    }

    private static Map<String, String> buildTimeseriesMap(String url, List<String> pathTokens, String type, String title, String cdid) {

        Map<String, String> documentMap = new HashMap<String, String>();
        documentMap.put(URI, url);
        documentMap.put(TYPE, type);
        documentMap.put(TITLE, title);
        documentMap.put(TAGS, pathTokens.toString());
        documentMap.put(CDID, cdid);
        return documentMap;
    }

    private static JsonObject getJsonObject(String absoluteFilePath) {
        JsonObject jsonObject;
        try {
            jsonObject = new JsonParser().parse(FileUtils.readFileToString(new File(absoluteFilePath), Charset.forName("UTF-8"))).getAsJsonObject();
        } catch (JsonSyntaxException | IOException e) {
            throw new RuntimeException("Failed to parse json: " + absoluteFilePath, e);
        }
        return jsonObject;
    }

    private static String getField(JsonObject jsonObject, String field) {
        if (StringUtils.isEmpty(field)) {
            throw new IllegalArgumentException("Field cannot be null");
        }

        JsonElement jsonElement = jsonObject.get(field);
        if (jsonElement == null) {
            return PageType.unknown.name();
        }

        return jsonElement.getAsString();
    }
}
