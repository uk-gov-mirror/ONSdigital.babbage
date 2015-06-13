package com.github.onsdigital.data;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.davidcarboni.ResourceUtils;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.content.service.ContentService;
import com.github.onsdigital.util.JsonPrettyprint;
import com.github.onsdigital.util.Validator;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import com.github.onsdigital.api.data.Data;
import com.github.onsdigital.configuration.Configuration;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

/**
 * Centralized service to serve data. Coded as a singleton rather than a collection of static methods for testing and mocking purposes.
 *
 * @author brn
 */
public class DataService implements ContentService {

    private static DataService instance = new DataService();

    private DataService() {
        triggerValidation();
    }


    public static DataService getInstance() {
        return instance;
    }

    @Override
    public InputStream readData(String uri) throws ContentNotFoundException {
        try {
            return getDataStream(uri);
        } catch (IOException e) {
            throw new RuntimeException("Failed reading data at " + uri);
        } catch (DataNotFoundException e) {
            throw new ContentNotFoundException(e);
        }
    }

    /**
     * @param uri
     * @return
     * @throws IOException
     */
    public String getDataAsString(String uri, boolean pretty) throws IOException {
        InputStream stream = getDataStream(uri);
        if (stream == null) {
            return "";
        }
        try (InputStreamReader reader = new InputStreamReader(stream, CharEncoding.UTF_8)) {
            return readJson(reader, pretty);
        }
    }

    /**
     * @param uriList List of data uris to be returned
     * @return requested data appended subsequently with given order
     * @throws IOException
     */
    public List<String> getDataAsString(List<String> uriList)
            throws IOException {
        ArrayList<String> data = new ArrayList<String>();
        for (String uri : uriList) {
            String dataString = getDataAsString(uri, false);
            if (dataString != null) {
                data.add(dataString);
            }
        }
        return data;

    }

    public InputStream getDataStream(String uriString)
            throws IOException {
        // Standardise the path:
        String uriPath = cleanPath(uriString);
        System.out.println("Reading data under uri:" + uriPath);
        Path taxonomy = FileSystems.getDefault().getPath(
                Configuration.getContentPath());

        // Look for a data.json file, or
        // fall back to adding a .json file extension
        Path data = taxonomy.resolve(uriPath).resolve("data.json");
        if (!Files.exists(data)) {
            data = taxonomy.resolve(uriPath + ".json");
        }
        if (Files.exists(data)) {
            return Files.newInputStream(data);
        }

        throw new DataNotFoundException(uriPath);
    }

    //Remove leading slash
    private String cleanPath(String uri) {
        return StringUtils.removeStart(uri, "/");
    }


    private String readJson(InputStreamReader reader, boolean pretty) throws IOException {
        StringWriter out = new StringWriter();

        JsonReader jsonReader = new JsonReader(reader);
        JsonWriter jsonWriter = new JsonWriter(out);
        if (pretty) {
            jsonWriter.setIndent("    ");
        }

        JsonPrettyprint.prettyprint(jsonReader, jsonWriter);
        jsonReader.close();
        jsonWriter.close();
        return out.toString();
    }


    private static void triggerValidation() {
        // Ensures ResourceUtils gets the right classloader when running
        // reloadable in development:
        ResourceUtils.classLoaderClass = Data.class;

        // Validate all Json so that we get a warning if
        // there's an issue with a file that's been edited.
        Validator.validate();

    }
}
