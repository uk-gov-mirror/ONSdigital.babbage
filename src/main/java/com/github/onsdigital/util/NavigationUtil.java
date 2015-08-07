package com.github.onsdigital.util;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.taxonomy.ProductPage;
import com.github.onsdigital.content.page.taxonomy.TaxonomyLandingPage;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NavigationUtil {

    private static Navigation navigation;
    private static long lastGenerated = 0; //milliseconds

    /**
     * Flag to avoid caching a broken navigation. This ensures it gets reloaded
     * if there's an error, allowing for a chance to fix it without restarting.
     */
    private static boolean jsonError;

    private NavigationUtil() {

    }

    public static Navigation getNavigation() throws IOException {
        if (navigation == null || isExpired()) {
            synchronized (NavigationUtil.class) {
                if (navigation == null || isExpired()) {
                    List<NavigationNode> nodes = buildNavigationNodes();
                    if (!jsonError) {
                        NavigationUtil.navigation = new Navigation();
                        navigation.setNodes(nodes);
                        lastGenerated = System.currentTimeMillis();
                    }
                }
            }
        }
        return navigation;
    }


    public static boolean isExpired() {
        if (Configuration.GENERAL.isDevelopment()) {
            return true;//No caching on dev environment
        }

        if (lastGenerated > 0) {
            return (System.currentTimeMillis() - lastGenerated) < TimeUnit.MINUTES.toMillis(Configuration.GENERAL.getGlobalCacheTimeout());
        }
        return false;
    }

    private static List<NavigationNode> buildNavigationNodes() throws IOException {
        List<NavigationNode> navigationNodes = new ArrayList<NavigationNode>();
        Path taxonomyPath = getContentPath();
        addNodes(navigationNodes, getNodes(taxonomyPath));
        for (NavigationNode node : navigationNodes) {
            node.children = new ArrayList<>();
            addNodes(node.children, getNodes(FileSystems.getDefault().getPath(taxonomyPath + "/" + node.fileName)));
        }
        return navigationNodes;
    }

    private static void addNodes(List<NavigationNode> nodeList, List<NavigationNode> toAdd) {

        int i = 0;
        for (NavigationNode navigationNode : toAdd) {
            nodeList.add(i, navigationNode);
            i++;
        }
        Collections.sort(nodeList);
    }

    private static List<NavigationNode> getNodes(Path path) throws IOException {
        List<NavigationNode> nodes = new ArrayList<NavigationNode>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path p : stream) {
                // Iterate over the paths:
                if (Files.isDirectory(p)) {
                    try {
                        NavigationNode node = getNavigationNode(p);
                        if (node != null) {
                            nodes.add(node);
                        }
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

    private static void sortNodes(List<NavigationNode> nodeList) {
        Collections.sort(nodeList);
    }

    private static NavigationNode getNavigationNode(Path path) throws IOException {
        NavigationNode result = null;

        Path dataJson = path.resolve("data.json");
        if (Files.exists(dataJson)) {
            try (InputStream input = Files.newInputStream(dataJson)) {
                Page page = ContentUtil.deserialisePage(input);
                if (page != null && page instanceof TaxonomyPage) {
                    if (page instanceof ProductPage) {
                        ProductPage productPage = (ProductPage) page;
                        if (isEmpty(productPage.getItems()) &&
                                isEmpty(productPage.getDatasets()) &&
                                isEmpty(productPage.getRelatedArticles()) &&
                                isEmpty(productPage.getStatsBulletins())) {
                            return null;//Skip if no data in the page at all
                        }
                    }

                    if(page instanceof TaxonomyLandingPage) {
                        TaxonomyLandingPage landingPage = (TaxonomyLandingPage) page;
                        if (isEmpty(landingPage.getSections())) {
                            return null;//skip landing page if no sub sections
                        }
                    }
                    TaxonomyPage taxonomyPage = (TaxonomyPage) page;
                    result = new NavigationNode(taxonomyPage);
                    result.fileName = path.getFileName().toString();
                }
            }
        }
        return result;
    }

    private static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }


    private static Path getContentPath() {
        return FileSystems.getDefault().getPath(Configuration.CONTENT_SERVICE.getContentPath());
    }

    public static void main(String[] args) {
        try {
            List<NavigationNode> nodes = NavigationUtil.buildNavigationNodes();
            for (NavigationNode navigationNode : nodes) {
                System.out.println(ReflectionToStringBuilder.toString(navigationNode));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
