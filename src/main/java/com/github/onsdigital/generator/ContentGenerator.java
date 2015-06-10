package com.github.onsdigital.generator;

import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.content.base.Content;
import com.github.onsdigital.content.link.PageReference;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.home.HomePage;
import com.github.onsdigital.content.page.methodology.Methodology;
import com.github.onsdigital.content.page.release.Release;
import com.github.onsdigital.content.page.statistics.Dataset;
import com.github.onsdigital.content.page.statistics.data.TimeSeries;
import com.github.onsdigital.content.page.statistics.data.base.StatisticalData;
import com.github.onsdigital.content.page.statistics.document.Article;
import com.github.onsdigital.content.page.statistics.document.Bulletin;
import com.github.onsdigital.content.page.taxonomy.ProductPage;
import com.github.onsdigital.content.page.taxonomy.TaxonomyLandingPage;
import com.github.onsdigital.content.page.taxonomy.base.TaxonomyPage;
import com.github.onsdigital.content.partial.HomeSection;
import com.github.onsdigital.content.partial.metadata.BulletinMetadata;
import com.github.onsdigital.content.partial.metadata.ReleaseMetadata;
import com.github.onsdigital.generator.data.Data;
import com.github.onsdigital.generator.data.DatasetMappingsCSV;
import com.github.onsdigital.generator.markdown.ArticleMarkdown;
import com.github.onsdigital.generator.markdown.BulletinMarkdown;
import com.github.onsdigital.generator.markdown.MethodologyMarkdown;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;


/**
 * Parses the taxonomy CSV file and generates a file structure and content data.json files
 */
public class ContentGenerator {


    private static final String CONTENTS_DIRECTORY = "src/main/content";
    private static final String RELEASES_DIRECTORY = "releases";
    private static final String BULLETINS_DIRECTORY = "bulletins";
    private static final String ARTICLES_DIRECTORY = "articles";
    private static final String METHODOLOGIES_DIRECTORY = "methodology";
    private static final String DATASETS_DIRECTORY = "datasets";
    private static final String TIMESERIES_DIRECTORY = "timeseries";


    private static final String DATA_FILE_NAME = "data.json";

    private File contentsDirectory;


    private HomePage homePage;
    private Map<URI, TaxonomyPage> themePages = new HashMap<>();
    private Map<URI, TimeSeries> generatedTimeSeries = new HashMap<>();
    private List<ContentNode> oldDatasetsCreated = new ArrayList<>();
    private Set<TimeSeries> noData = new TreeSet<>();
    private Map<ProductPage, Release> releases = new HashMap<>();


    private void createReleases() throws FileNotFoundException, IOException {

        File releasesFolder = createSubDirectory(contentsDirectory, RELEASES_DIRECTORY);
        List<ReleaseMetadata> releasesList = new ArrayList<>();
        for (Release release : releases.values()) {
            File releaseFolder = createDirectory(CONTENTS_DIRECTORY + "/" + release.uri.toString());
            persistData(releaseFolder, release);
            releasesList.add(new ReleaseMetadata(release));
        }

        File releasesFile = new File(releasesFolder, DATA_FILE_NAME);
        try (OutputStream output = new FileOutputStream(releasesFile)) {
            Serialiser.serialise(output, new ReleasesList(releasesList));
        }
    }

    static class ReleasesList {
        List<ReleaseMetadata> releases;

        ReleasesList(List<ReleaseMetadata> releases) {
            this.releases = releases;
        }
    }


    public void generate() throws IOException {

        // Set up the folders and trigger CSV parsing:
        Data.parse();
        contentsDirectory = initializeContentsDirectory();

        ContentNode rootNode = new ContentNode();
        rootNode.addChildren(Data.folders());

        homePage = generateHomepage(rootNode);
        generateThemePages(contentsDirectory, rootNode, homePage);

        setHomepageDefaults();
        persistData(contentsDirectory, homePage);

        // Releases:
        createReleases();

    }

    private HomePage generateHomepage(ContentNode rootNode) throws IOException {
        // The folder needs to be at the root path:
        HomePage homePage = new HomePage();
        if (rootNode.oldDataset.size() > 0) {
            throw new RuntimeException("A dataset has been mapped to " + rootNode + " but this folder is the root content folder.");
        }
        return homePage;
    }


    private void buildHomeSections(HomePage homePage, List<TaxonomyPage> taxonomyPages) {
        for (TaxonomyPage taxonomyPage : taxonomyPages) {
            if (taxonomyPage instanceof TaxonomyLandingPage == false) {
                throw new RuntimeException("Taxonomy page " + taxonomyPage.title + " is not a taxonomy landing page");
            }
        }
    }


    private void generateThemePages(File parentDirectory, ContentNode contentNode, HomePage homePage) throws IOException {
        for (ContentNode node : contentNode.getChildren()) {
            //Create folder for taxonomy page
            File directory = createSubDirectory(parentDirectory, node.filename());
            System.out.println("Content Folder  : " + directory.getAbsolutePath());

            if (node.getChildren().size() == 0) {
                throw new RuntimeException("Could not find any folders under theme " + contentNode.name);
            }

            TaxonomyLandingPage themepage = generateTaxonomyLandingPage(directory, node, homePage);
            generateSubLevels(directory, node, themepage);
            themePages.put(themepage.uri, themepage);
            persistData(directory, themepage);
        }
    }

    private void generateSubLevels(File parentDirectory, ContentNode contentNode, TaxonomyLandingPage parent) throws IOException {
        for (ContentNode node : contentNode.getChildren()) {
            //Create folder for taxonomy page
            File directory = createSubDirectory(parentDirectory, node.filename());
            System.out.println("Content Folder  : " + directory.getAbsolutePath());

            TaxonomyPage taxonomyPage = null;
            //No children means this is a product page, otherwise a taxonomy landing page
            if (node.getChildren().size() == 0) {
                taxonomyPage = generateProductPage(directory, node, parent);
            } else {
                TaxonomyLandingPage taxonomyLandingPage = generateTaxonomyLandingPage(directory, node, parent);
                //Recursively create sub folders and data
                generateSubLevels(directory, node, taxonomyLandingPage);
                taxonomyPage = taxonomyLandingPage;
            }

            //Each taxonomy page recursively added to its parent's sections
            parent.sections.add(new PageReference<>(taxonomyPage, node.index));
            persistData(directory, taxonomyPage);
        }
        Collections.sort(parent.sections);
    }

    private TaxonomyLandingPage generateTaxonomyLandingPage(File directory, ContentNode node, TaxonomyPage parent) throws IOException {
        TaxonomyLandingPage landingPage = new TaxonomyLandingPage();
        landingPage.title = node.name;
        landingPage.uri = createUri(node.filename(), parent);
        landingPage.summary = node.lede;
        landingPage.index = node.index;
        landingPage.buildBreadcrumb(parent);
        if (node.oldDataset.size() > 0) {
            throw new RuntimeException("A dataset has been mapped to " + node + " but this folder is a Taxonomy Landing page.");
        }
        return landingPage;
    }


    private ProductPage generateProductPage(File directory, ContentNode node, TaxonomyPage parent) throws IOException {
        ProductPage productPage = new ProductPage();
        productPage.title = node.name;
        productPage.uri = createUri(node.filename(), parent);
        productPage.summary = node.lede;
        productPage.index = node.index;
        productPage.buildBreadcrumb(parent);

        addTimeseriesReferences(node, productPage);

        createStatsBulletinHeadline(node, productPage);
        createStatsBulletins(node, productPage);
        createDatasets(node, productPage);

        persistData(directory, productPage);

        persistBulletins(node, directory, productPage);
        persistArticles(node, directory, productPage);
        persistMethodologies(node, directory, productPage);
        persistDatasets(node, directory, productPage);


        createTimeseries(node, directory, productPage);

        Release release = new Release();
        release.title = productPage.title;
        release.summary = productPage.summary;
        release.uri = URI.create(RELEASES_DIRECTORY + "/" + node.filename());
        releases.put(productPage, release);

        return productPage;
    }

    private void addTimeseriesReferences(ContentNode node, ProductPage productPage) {
        TimeSeries headline = node.headline;
        // Timeseries references:
        if (headline == null || headline.uri == null) {
            System.out.println("No headline URI set for " + node.name);
            headline = node.timeserieses.get(0);
            System.out.println("Using the first item from the timeseries list instead: " + headline);
        }

        productPage.headline = new PageReference<StatisticalData>(headline);

        List<TimeSeries> timeserieses = node.timeserieses;
        productPage.items = new ArrayList<>();

        for (TimeSeries timeseries : timeserieses) {
            if (timeseries.uri != null) {
                productPage.items.add(new PageReference<StatisticalData>(timeseries));
            } else {
                System.out.println("No URI set for " + timeseries);
            }
        }
    }

//    private static List<ContentNode> getT3Folders(ContentNode folder) {
//        List<ContentNode> result = new ArrayList<ContentNode>();
//
//        // If the folder is t3, add it directly:
//        if (folder.getChildren().size() == 0) {
//            result.add(folder);
//        }
//
//        // If it's a t2, recurse:
//        for (ContentNode child : folder.getChildren()) {
//            result.addAll(getT3Folders(child));
//        }
//        return result;
//    }

//    private static Set<URI> getTimeseries(List<ContentNode> t3Folders) throws IOException {
//        // Keep keys in the order they are added, but allow for de-duplication:
//        Set<URI> result = new LinkedHashSet<>();
//
//        // Add the headlines first so that they will appear first
//        for (ContentNode t3Folder : t3Folders) {
//            if (t3Folder.headline != null) {
//                result.add(t3Folder.headline.uri);
//            }
//        }
//
//        // Add the other items in case there aren't enough headline items:
//        for (ContentNode t3Folder : t3Folders) {
//            // Limit the number in case we have thousands
//            // (this is quite likely at some point)
//            int max = 4;
//            for (Timeseries timeseries : t3Folder.timeserieses) {
//                if (max-- < 0) {
//                    if (timeseries.uri != null) {
//                        result.add(timeseries.uri);
//                    } else {
//                        System.out.println("No URI defined for " + timeseries + " when scanning to " + t3Folder);
//                    }
//                }
//            }
//        }
//
//        return result;
//    }

    private void createDatasets(ContentNode folder, ProductPage productPage) throws IOException {
        productPage.datasets = new ArrayList<>();

        for (Dataset dataset : folder.datasets) {
            if (dataset.summary != null) {
                if (dataset.uri == null) {
                    dataset.uri = toDatasetUri(folder, dataset);
                }
                productPage.datasets.add(new PageReference<>(dataset));
            }
        }
    }

    private static void createStatsBulletins(ContentNode folder, ProductPage productPage) throws IOException {
        productPage.statsBulletins = new ArrayList<>();

        for (Bulletin bulletin : folder.bulletins) {
            if (bulletin.uri == null) {
                bulletin.uri = toStatsBulletinUri(BulletinMarkdown.toFilename(bulletin), bulletin, productPage);
            }
            productPage.statsBulletins.add(new PageReference<>(bulletin));
        }
        if (folder.additonalBulletin != null) {
            if (folder.additonalBulletin.uri == null) {
                throw new RuntimeException("No URI yet - this is a design issue.");
            }
            productPage.statsBulletins.add(new PageReference<>(folder.additonalBulletin));
        }

        // All bulletins at this node, plus the additional bulletin (if any) are
        // considered to be related.
        // This is "good enough" for now:
        for (Bulletin bulletin : folder.bulletins) {

            // Initially add everything - we'll remove "self-reference"
            // afterwards:
            bulletin.relatedBulletins.addAll(productPage.statsBulletins);

            // Now remove self-references:
            Iterator<PageReference<Bulletin>> iterator = bulletin.relatedBulletins.iterator();
            while (iterator.hasNext()) {
                PageReference next = iterator.next();
                if (next == null || next.getUri() == null || bulletin == null || bulletin.uri == null) {
                    System.out.println("wat?");
                }
                if (next.getUri().equals(bulletin.uri)) {
                    iterator.remove();
                }
            }
        }
    }

    private static void createStatsBulletinHeadline(ContentNode node, ProductPage productPage) throws IOException {
        // Stats bulletin references:

        if (node.headlineBulletin == null) {
            if (node.bulletins.size() > 0) {
                node.headlineBulletin = node.bulletins.get(0);
            } else if (node.additonalBulletin != null) {
                node.headlineBulletin = node.additonalBulletin;
            }
        }

        if (node.headlineBulletin != null) {
            if (node.headlineBulletin.uri == null) {
                node.headlineBulletin.uri = toStatsBulletinUri(BulletinMarkdown.toFilename(node.headlineBulletin), node.headlineBulletin, productPage);
            }
            productPage.statsBulletinHeadline = new PageReference<Bulletin>(node.headlineBulletin);
        }
    }


    private static URI createUri(String fileName, Page parent) {
        String sanitizedFilename = fileName.replaceAll("\\W", "");
        String parentUri = (parent != null && !"/".equals(parent.uri.toString())) ? parent.uri.toString() : "";
        return URI.create(parentUri + "/" + StringUtils.deleteWhitespace(sanitizedFilename));
    }

    private static URI toStatsBulletinUri(String fileName, Bulletin bulletin, ProductPage productPage) {

        String baseUri = productPage.uri + "/bulletins";
        String bulletinFileName = fileName;
        if (bulletinFileName == null) {
            System.out.println("No filename for : " + bulletin.title);
        }
        String sanitizedBulletinFileName = bulletinFileName.replaceAll("\\W", "");
        return URI.create(baseUri + "/" + StringUtils.deleteWhitespace(sanitizedBulletinFileName));
    }

    private URI toDatasetUri(ContentNode folder, Dataset dataset) {

        String baseUri = "/" + folder.filename();
        ContentNode parent = folder.parent;
        while (parent != null) {
            baseUri = "/" + parent.filename() + baseUri;
            parent = parent.parent;
        }
        baseUri += "/datasets";
        String datasetFileName = dataset.title;
        String sanitizedDatasetFileName = datasetFileName.replaceAll("\\W", "");
        return URI.create(baseUri + "/" + StringUtils.deleteWhitespace(sanitizedDatasetFileName));
    }

    /**
     * Creates timeseries data.
     *
     * @param folder
     * @param file
     * @param t3
     * @throws IOException
     */
    private void createTimeseries(ContentNode folder, File file, ProductPage t3) throws IOException {

        int created = 0;

        Set<TimeSeries> timeserieses = new HashSet<>(folder.timeserieses);
        if (folder.headline != null) {
            timeserieses.add(folder.headline);
        }

        // Write out timeseries specified by the Alpha Content spreadsheet:
        for (TimeSeries timeseries : timeserieses) {

            if (createTimeseries(timeseries, folder, t3)) {
                created++;
            }
        }

        // TODO: Other timeseries mappings are commented out to minimise volume
        // of files for now:
        // // Write out timeseries mapped according to the "old dataset"
        // // taxonomy map:
        // Set<Timeseries> total = new HashSet<Timeseries>(timeserieses);
        // if (folder.oldDataset.size() > 0) {
        // oldDatasetsCreated.add(folder);
        // for (Set<Timeseries> dataset : folder.oldDataset) {
        // for (Timeseries timeseries : dataset) {
        //
        // if (createTimeseries(timeseries, t3)) {
        // created++;
        // }
        // }
        // total.addAll(dataset);
        // }
        //
        // System.out.println("Referenced CDIDs vs. total CDIDs at this node: "
        // + timeserieses.size() + "/" + total.size() + " (" + created +
        // " created)");
        // }
    }

    private boolean createTimeseries(TimeSeries timeseries, ContentNode folder, ProductPage productPage) throws IOException {
        boolean result = false;

        URI uri = timeseries.uri;
        File timeseriesFolder = new File(contentsDirectory, uri.toString());
        File timeseriesFile = new File(timeseriesFolder, DATA_FILE_NAME);

        if (uri.toString().contains(productPage.uri.toString())) {
            // Only create the timeseries if it doesn't already exist:
            if (!timeseriesFile.exists()) {
                timeseriesFolder.mkdirs();

                timeseries.buildBreadcrumb(productPage);
                if (timeseries.months.size() == 0 && timeseries.quarters.size() == 0 && timeseries.years.size() == 0) {
                    noData.add(timeseries);
                }

                for (Bulletin bulletin : folder.bulletins) {
                    timeseries.relatedDocuments.add(new PageReference<>(bulletin));
                }

                List<TimeSeries> relatedCdids = Data.relatedTimeseries(timeseries);
                if (relatedCdids != null && !relatedCdids.isEmpty()) {
                    for (TimeSeries relatedCdid : relatedCdids) {
                        TimeSeries relatedTimeseries = Data.timeseries(relatedCdid.cdid);
                        timeseries.relatedTimeseries.add(new PageReference<>(relatedTimeseries));
                    }
                }

                persistData(timeseriesFolder, timeseries);
                generatedTimeSeries.put(timeseries.uri, timeseries);
                result = true;
            }
        }
        return result;
    }

    private void persistBulletins(ContentNode folder, File file, ProductPage productPage) throws IOException {
        if (folder.bulletins.size() > 0) {
            File bulletinsDir = createSubDirectory(file, BULLETINS_DIRECTORY);
            for (Bulletin bulletin : folder.bulletins) {
                bulletin.buildBreadcrumb(productPage);
                File bulletinDir = createSubDirectory(bulletinsDir, StringUtils.deleteWhitespace(BulletinMarkdown.toFilename(bulletin)));
                persistData(bulletinDir, bulletin);
            }
        }
    }

    private void persistArticles(ContentNode folder, File file, ProductPage productPage) throws IOException {
        if (folder.articles.size() > 0) {
            File articlesDir = createSubDirectory(file, ARTICLES_DIRECTORY);
            for (Article article : folder.articles) {
                article.buildBreadcrumb(productPage);
                File articleDir = createSubDirectory(articlesDir, StringUtils.deleteWhitespace(ArticleMarkdown.toFilename(article)));
                persistData(articleDir, article);
            }
        }
    }

    private void persistMethodologies(ContentNode folder, File file, ProductPage productPage) throws IOException {
        if (folder.methodology.size() > 0) {
            File methodologiesDir = createSubDirectory(file, METHODOLOGIES_DIRECTORY);
            for (Methodology methodology : folder.methodology) {
                methodology.buildBreadcrumb(productPage);
                File methodologyDir = createSubDirectory(methodologiesDir, StringUtils.deleteWhitespace(MethodologyMarkdown.toFilename(methodology)));
                persistData(methodologyDir, methodology);
            }
        }
    }

    private void persistDatasets(ContentNode folder, File file, ProductPage productPage) throws IOException {

        if (folder.datasets.size() > 0) {
            File datasetsFolder = createSubDirectory(file, DATASETS_DIRECTORY);
            for (Dataset dataset : folder.datasets) {
                dataset.buildBreadcrumb(productPage);
                String datasetFileName = dataset.title.replaceAll("\\W", "");
                File bulletinDir = createSubDirectory(datasetsFolder, datasetFileName.toLowerCase());
                persistData(bulletinDir, dataset);
            }
        }
    }

    //Creates content directory. If it already exists deletes it
    private File initializeContentsDirectory() throws IOException {
        // Walk folder tree:
        System.out.printf("Deleting content directory");
        deleteDirectory(CONTENTS_DIRECTORY);
        return createDirectory(CONTENTS_DIRECTORY);
    }

    //Create folder with given title under given parent paths
    private File createSubDirectory(File parent, String folderName) {
        File directory = new File(parent, folderName);
        boolean created = directory.mkdirs();
        if (created) {
            return directory;
        }
        throw new RuntimeException("Failed creating folder " + folderName + " under " + parent.getAbsolutePath());
    }

    private void deleteDirectory(String path) throws IOException {
        FileUtils.deleteDirectory(new File(path));
    }

    private File createDirectory(String path) {
        File directory = new File(path);
        boolean created = directory.mkdirs();
        if (created) {
            return directory;
        }
        throw new RuntimeException("Failed creating folder" + path);
    }

    //Persists content data as json in given folder (data.json)
    private void persistData(File folder, Content content) throws IOException {
        FileUtils.writeStringToFile(new File(folder, DATA_FILE_NAME), content.toJson(), Charset.forName("UTF8"));
    }


    private void setHomepageDefaults() {
        homePage.sections = new ArrayList<>();

        homePage.sections.add(createHomeSection("/economy", "/economy/inflationandpriceindices/timeseries/d7g7"));
        homePage.sections.add(createHomeSection("/economy", "/economy/grossdomesticproductgdp/timeseries/ihyq"));
        homePage.sections.add(createHomeSection("/businessindustryandtrade", "/businessindustryandtrade/internationaltrade/timeseries/ikbj"));
        homePage.sections.add(createHomeSection("/employmentandlabourmarket", "/employmentandlabourmarket/peopleinwork/employmentandemployeetypes/timeseries/lf24"));
        homePage.sections.add(createHomeSection("/peoplepopulationandcommunity", "/peoplepopulationandcommunity/populationandmigration/populationestimates/timeseries/raid121"));

    }

    private HomeSection createHomeSection(String landingPageUri, String timeseriesUri) {
        TimeSeries timeseries = generatedTimeSeries.get(URI.create(timeseriesUri));
        TaxonomyLandingPage taxonomyLandingPage = (TaxonomyLandingPage) themePages.get(URI.create(landingPageUri));

        if (timeseries == null) {
            throw new RuntimeException("Could not find timeseries " + timeseriesUri);
        }
        if (taxonomyLandingPage == null) {
            throw new RuntimeException("Could not find landing page " + landingPageUri);
        }

        PageReference timeseriesReference = new PageReference(timeseries);
        PageReference landingPageReference = new PageReference<>(taxonomyLandingPage);

        return new HomeSection(landingPageReference, timeseriesReference);
    }

    public static void main(String[] args) throws IOException {

//		Serialiser.getBuilder().setPrettyPrinting();

        ContentGenerator generator = new ContentGenerator();
        generator.generate();

        // Print out metrics and warnings that provide information on whether
        // the process is working as expected:

        // System.out.println(Data.getDateLabels());
        System.out.println("Timeseries with no data: " + generator.noData + " (" + generator.noData.size() + ")");
        System.out.println("You have a grand total of " + generator.generatedTimeSeries.size() + " timeseries, out of a total possible " + Data.size() + " parsed timeseries.");
        System.out.println("There are a total of " + Data.sizeOldDatasets() + " CDIDs classified into one or more datasets.");
        Set<String> unmappedDatasets = Data.unmappedOldDatasets();
        if (unmappedDatasets.size() > 0) {
            System.out.println("To increase this number, please add mappings for the following datasets: " + unmappedDatasets);
            for (String datasetName : unmappedDatasets) {
                if (!"other".equals(datasetName)) {
                    System.out.println(" - " + datasetName + " contains " + Data.oldDataset(datasetName).size());
                }
            }
        }
        if (Data.oldDataset("other") != null) {
            System.out.println("The 'other' dataset contains " + Data.oldDataset("other").size() + " timeseries.");
        }

        Set<ContentNode> mapped = new TreeSet<>();
        for (ContentNode folder : DatasetMappingsCSV.mappedFolders.values()) {
            mapped.add(folder);
        }
        if (generator.oldDatasetsCreated.size() > 0 && generator.oldDatasetsCreated.size() != mapped.size()) {
            System.out.println(generator.oldDatasetsCreated.size() + " old datasets have been created, from a total of " + Data.sizeOldDatasetsCount());
            Collections.sort(generator.oldDatasetsCreated);
            for (ContentNode folder : generator.oldDatasetsCreated) {
                System.out.println(" - " + folder.path());
            }
            System.out.println("Expected the following:");
            for (ContentNode folder : mapped) {
                System.out.println(" - " + folder.path());
            }
        }

        int yes = 0;
        int no = 0;
        int missing = 0;
        for (TimeSeries timeseries : new Data()) {
            if (timeseries.uri == null) {
                missing++;
            } else {
                File path = new File(generator.contentsDirectory, timeseries.uri.toString());
                path = new File(path, DATA_FILE_NAME);
                if (path.exists()) {
                    yes++;
                } else {
                    no++;
                }
            }
            if (!timeseries.cdid.matches("[A-Z0-9]{3,8}")) {
                throw new RuntimeException("CDID " + timeseries + " is not in the expected format.");
            }
        }
        System.out.println(yes + " timeseries have been verified to exist on disk.");
        if (no > 0) {
            System.out.println("Warning: " + no + " timeseries don't actually exist on disk");
        }
        if (missing > 0) {
            System.out.println(missing + " timeseries have no URI set (suggesting they can't be written to the taxonomy)");
        }
    }


}
