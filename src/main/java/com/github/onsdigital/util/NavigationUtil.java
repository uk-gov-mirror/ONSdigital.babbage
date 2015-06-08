package com.github.onsdigital.util;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.partial.link.ContentLink;
import com.github.onsdigital.content.serialiser.ContentSerialiser;
import com.github.onsdigital.content.taxonomy.base.TaxonomyPage;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.w3c.dom.DOMException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NavigationUtil {

    private static List<NavigationNode> navigation;

    /**
     * Flag to avoid caching a broken navigation. This ensures it gets reloaded
     * if there's an error, allowing for a chance to fix it without restarting.
     */
    private static boolean jsonError;

    private NavigationUtil() {

    }

    public static List<NavigationNode> getNavigationNodes() throws IOException {
        if (navigation == null) {
            synchronized (NavigationUtil.class) {
                if (navigation == null) {
                    buildNavigationNodes();
                }
            }
        }
        return navigation;
    }

    private static void buildNavigationNodes() throws IOException {
        List<NavigationNode> navigation = new ArrayList<NavigationUtil.NavigationNode>();
        Path taxonomyPath = getHomePath();
        addNodes(navigation, getNodes(taxonomyPath));
        for (NavigationNode node : navigation) {
            addNodes(node.children, getNodes(FileSystems.getDefault().getPath(taxonomyPath + "/" + node.uri)));
        }
        if (!jsonError) {
            NavigationUtil.navigation = navigation;
        }
    }

    private static void addNodes(List<NavigationNode> nodeList, List<NavigationNode> toAdd) {
        Collections.sort(toAdd);
        int i = 0;
        for (NavigationNode navigationNode : toAdd) {
            nodeList.add(i, navigationNode);
            i++;
        }
    }

    private static List<NavigationNode> getNodes(Path path) throws IOException {
        List<NavigationNode> nodes = new ArrayList<NavigationNode>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path p : stream) {
                // Iterate over the paths:
                if (Files.isDirectory(p) && isNotRelease(p)) {
                    try {
                        nodes.add(new NavigationNode(getDataJson(p)));
                    } catch (JsonSyntaxException e) {
                        jsonError = true;
                        System.out.println("Navigation: malformed Json, omitting: " + p);
                    }
                }
            }
            return nodes;
        } catch (DOMException | MalformedURLException e) {
            throw new IOException("Error iterating taxonomy", e);
        }
    }

    private static Path getHomePath() {
        return FileSystems.getDefault().getPath(Configuration.getContentPath());
    }

    private static ContentLink<TaxonomyPage> getDataJson(Path path) throws IOException {
        ContentLink result = null;

        Path dataJson = path.resolve("data.json");
        if (Files.exists(dataJson)) {
            try (InputStream input = Files.newInputStream(dataJson)) {
                TaxonomyPage taxonomyPage = new ContentSerialiser().deserialise(input, TaxonomyPage.class);
                result = new ContentLink<>(taxonomyPage, taxonomyPage.index);
            }
        }
        return result;
    }

    private static boolean isNotRelease(Path p) {
        return !p.getFileName().toString().contains("releases");
    }

    public static class NavigationNode implements Comparable<NavigationNode> {
        String name;
        String url;
        URI uri;
        int index;
        List<NavigationNode> children = new ArrayList<NavigationNode>();

        public NavigationNode(ContentLink<TaxonomyPage> taxonomy) {
            this.name = taxonomy.name;
            this.uri = taxonomy.uri;
            this.index = taxonomy.index;
            url = taxonomy.uri.toString();
        }

        @Override
        public int compareTo(NavigationNode o) {
            return Integer.compare(this.index, o.index);
        }

    }

    public static void main(String[] args) {
        try {
            List<NavigationNode> nodes = NavigationUtil.getNavigationNodes();
            for (NavigationNode navigationNode : nodes) {
                System.out.println(ReflectionToStringBuilder.toString(navigationNode));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
