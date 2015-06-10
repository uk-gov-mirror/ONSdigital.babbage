package com.github.onsdigital.util;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.taxonomy.base.TaxonomyPage;
import com.github.onsdigital.content.partial.navigation.Navigation;
import com.github.onsdigital.content.partial.navigation.NavigationNode;
import com.github.onsdigital.content.util.ContentUtil;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.w3c.dom.DOMException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NavigationUtil {

    private static Navigation navigation;

    /**
     * Flag to avoid caching a broken navigation. This ensures it gets reloaded
     * if there's an error, allowing for a chance to fix it without restarting.
     */
    private static boolean jsonError;

    private NavigationUtil() {

    }

    public static Navigation getNavigation() throws IOException {
        if (navigation == null) {
            synchronized (NavigationUtil.class) {
                if (navigation == null) {
                    buildNavigation();
                }
            }
        }
        return navigation;
    }

    private static void buildNavigation() throws IOException {
        try {
            List<NavigationNode> nodes = getNavigationNodes();
            Navigation navigation = new Navigation();
            navigation.nodes = nodes;
            Collections.sort(nodes);
            if (!jsonError) {
                NavigationUtil.navigation = navigation;
            }

        } catch (DOMException | MalformedURLException e) {
            throw new IOException("Error iterating taxonomy", e);
        }
    }

    private static List<NavigationNode> getNavigationNodes() throws IOException {
        List<NavigationNode> nodes = new ArrayList<>();
        DirectoryStream<Path> stream = Files.newDirectoryStream(getContentPath());
        for (Path p : stream) {
            // Iterate over the paths:
            if (Files.isDirectory(p) && isTaxonomy(p)) {
                try {
                    nodes.add(getNavigationNode(p));
                } catch (JsonSyntaxException e) {
                    jsonError = true;
                    System.out.println("Navigation: malformed Json, omitting: " + p);
                }
            }
        }
        return nodes;
    }

    private static void sortNodes(List<NavigationNode> nodeList) {
        Collections.sort(nodeList);
    }

    private static NavigationNode getNavigationNode(Path path) throws IOException {
        NavigationNode result = null;

        Path dataJson = path.resolve("data.json");
        if (Files.exists(dataJson)) {
            try (InputStream input = Files.newInputStream(dataJson)) {
                TaxonomyPage taxonomyPage = (TaxonomyPage) ContentUtil.deserialisePage(input);
                result = new NavigationNode(taxonomyPage);
            }
        }
        return result;
    }


    private static Path getContentPath() {
        return FileSystems.getDefault().getPath(Configuration.getContentPath());
    }

    private static boolean isTaxonomy(Path p) {
        return !p.getFileName().toString().contains("releases");
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
