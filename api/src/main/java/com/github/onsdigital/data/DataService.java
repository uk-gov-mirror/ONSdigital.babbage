package com.github.onsdigital.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.davidcarboni.ResourceUtils;
import com.github.onsdigital.util.Validator;
import org.apache.commons.io.IOUtils;

import com.github.onsdigital.api.data.Data;
import com.github.onsdigital.configuration.Configuration;

/**
 * Centralized service to serve data. Coded as a singleton rather than a collection of static methods for testing and mocking purposes.
 *
 * @author brn
 */
public class DataService {

    private static DataService instance = new DataService();

    private DataService() {
        triggerValidation();
    }


    public static DataService getInstance() {
        return instance;
    }

    /**
     * @param uri
     * @return
     * @throws IOException
     */
    public String getDataAsString(String uri) throws IOException {
        InputStream stream = getDataStream(uri);
        if (stream == null) {
            return "";
        }
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            return IOUtils.toString(reader);
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
            String dataString = getDataAsString(uri);
            if (dataString != null) {
                data.add(dataString);
            }
        }
        return data;

    }

    public InputStream getDataStream(String uriString)
            throws IOException {
        // Standardise the path:
        URI uri = URI.create(uriString);
        String uriPath = cleanPath(uri);
        Path taxonomy = FileSystems.getDefault().getPath(
                Configuration.getTaxonomyPath());

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

    /**
     * @param uri The URI to get a standardised path from.
     * @return The URI path, lowercased, without the endpoint name or trailing
     * slash.
     */
    public String cleanPath(URI uri) {

        // It would be nice to use StringBuilder,
        // but it doesn't have the manipulation methods we need
        String result = uri.getPath();

        // Remove endpoint name:
        String endpointName = "/" + Data.class.getSimpleName().toLowerCase();
        if (result.startsWith(endpointName)) {
            result = result.substring(endpointName.length());
        }

        // Remove slashes:
        if (result.startsWith("/")) {
            result = result.substring(1);
        }
        if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }

        // Lowercase
        result = result.toLowerCase();

        return result;
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
