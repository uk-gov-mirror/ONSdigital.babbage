package com.github.onsdigital.generator.data;

import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.page.statistics.dataset.Dataset;
import com.github.onsdigital.content.partial.TimeseriesValue;
import com.github.onsdigital.generator.ContentNode;
import com.github.onsdigital.generator.datasets.DatasetContent;
import com.github.onsdigital.generator.markdown.AdditionalBulletins;
import com.github.onsdigital.generator.markdown.ArticleMarkdown;
import com.github.onsdigital.generator.markdown.BulletinMarkdown;
import com.github.onsdigital.generator.taxonomy.TaxonomyCsv;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * This class provides a structure for holding data used in building the
 * taxonomy.
 * <p>
 * There are quite a number of CSV files that need to be parsed, so this enables
 * the information from all those files to be collected and layered into a
 * single set of objects, ready to be written to disk by the taxonomy generator.
 *
 * @author david
 */
public class Data implements Iterable<TimeSeries> {

    public static Map<Date, String> years = new TreeMap<>();
    public static Map<Date, String> yearEnds = new TreeMap<>();
    public static Map<Date, String> yearIntervals = new TreeMap<>();
    public static Map<Date, String> yearPairs = new TreeMap<>();
    public static Map<Date, String> quarters = new TreeMap<>();
    public static Map<Date, String> months = new TreeMap<>();
    public static Map<TimeSeries, List<TimeSeries>> relatedTimeseries = new HashMap<>();

    private static Set<ContentNode> folders;
    private static Set<Dataset> datasets = new HashSet<Dataset>();
    private static Map<String, Set<TimeSeries>> oldDatasets = new TreeMap<>();
    private static Map<String, TimeSeries> timeserieses = new HashMap<>();
    private static Set<String> mappedDatasets = new HashSet<>();

    /**
     * Triggers parsing of {@link NonCdidCSV}, {@link DataCSV},
     * {@link MetadataCSV} and {@link AlphaContentCSV}.
     *
     * @throws IOException If an error occurs during parsing.
     */
    public static void parse() throws IOException {

        System.out.println("Starting parsing of spreadsheets...");

        folders = TaxonomyCsv.parse();

        // Basic data
        MetadataCSV.parse();

        // Main manually set-up spreadsheet
        // may overwrite basic data:
        AlphaContentCSV.parse();
        DatasetContent.parse();

        // Markdown content:
        BulletinMarkdown.parse();
        ArticleMarkdown.parse();
        // MethodologyMarkdown.parse();
        AdditionalBulletins.parse();

        // Data
        NonCdidCSV.parse();
        DataCSV.parse();
        DatasetMappingsCSV.parse();

        // Only call this if you want to reset bulletin content using the
        // bulletins sheet of the Alpha Content Spreadsheet.
        // BulletinContent.parseCsv();

        System.out.println("Parsing complete.");
    }

    public static void addDateOption(String date) {

        TimeseriesValue value = new TimeseriesValue();
        value.date = date;
        Date key = value.toDate();
        String standardised = toKey(date);

        // Pick the correct list for this option:
        if (TimeSeries.year.matcher(toKey(date)).matches()) {
            years.put(key, date);
        } else if (TimeSeries.yearEnd.matcher(standardised).matches()) {
            yearEnds.put(key, date);
        } else if (TimeSeries.yearInterval.matcher(standardised).matches()) {
            yearIntervals.put(key, date);
        } else if (TimeSeries.yearPair.matcher(standardised).matches()) {
            yearPairs.put(key, date);
        } else if (TimeSeries.month.matcher(standardised).matches()) {
            months.put(key, date);
        } else if (TimeSeries.quarter.matcher(standardised).matches()) {
            quarters.put(key, date);
        } else {
            throw new IllegalArgumentException("Unknow date format: '" + date + "'");
        }
    }

    public static Set<ContentNode> folders() {
        return folders;
    }

    /**
     * Gets a {@link ContentNode} from the taxonomy, basend on a specification of
     * theme, level2 and level3 folder names.
     *
     * @param theme  The theme folder.
     * @param level2 The level 2 folder. Can be null if you wish to get a theme
     *               folder.
     * @param level3 The level 3 folder. Can be null if you wish to get a level 3
     *               folder.
     * @return
     */
    public static ContentNode getFolder(String theme, String level2, String level3) {
        ContentNode result = null;

        ContentNode themeFolder = null;
        ContentNode level2Folder = null;
        ContentNode level3Folder = null;

        // Locate the theme folder
        for (ContentNode folder : folders) {
            if (StringUtils.equalsIgnoreCase(folder.name, theme)) {
                themeFolder = folder;
                result = folder;
            }
        }
        if (themeFolder == null) {
            throw new RuntimeException(theme + " is not a theme folder in the taxonomy.");
        }

        if (StringUtils.isNotBlank(level2)) {
            // Locate the level 2 folder
            for (ContentNode folder : themeFolder.getChildren()) {
                if (StringUtils.equalsIgnoreCase(folder.name, level2)) {
                    level2Folder = folder;
                    result = folder;
                }
            }
            if (level2Folder == null) {
                throw new RuntimeException(level2 + " is not a level 2 folder in the taxonomy.");
            }
        }

        if (StringUtils.isNotBlank(level3)) {
            // Locate the level 2 folder
            for (ContentNode folder : level2Folder.getChildren()) {
                if (StringUtils.equalsIgnoreCase(folder.name, level3)) {
                    level3Folder = folder;
                    result = folder;
                }
            }
            if (level3Folder == null) {
                throw new RuntimeException(level3 + " is not a level 3 folder in the taxonomy.");
            }
        }

        return result;
    }

    /**
     * Gets the specified dataset.
     *
     * @param name The title of the dataset.
     * @return The specified dataset, or null if it is not present.
     */
    public static Set<TimeSeries> oldDataset(String name) {

        return oldDatasets.get(toKey(name));
    }

    /**
     * Gets the specified timeseries.
     *
     * @param cdid The CDID identifier for the timeseries.
     * @return The specified timeseries, or null if it is not present.
     */
    public static TimeSeries timeseries(String cdid) {
        return timeserieses.get(toKey(cdid));
    }

    /**
     * Adds a new timeseries. If the timeseries is already present, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param timeseries The timeseries.
     */
    public static void addTimeseries(TimeSeries timeseries) {
        if (timeserieses.containsKey(toKey(timeseries.getCdid()))) {
            throw new IllegalArgumentException("Duplicate timeseries: " + timeseries);
        }
        timeserieses.put(toKey(timeseries.getCdid()), timeseries);
    }

    /**
     * Creates and adds a new timeseries. If the timeseries is already present,
     * an {@link IllegalArgumentException} is thrown.
     *
     * @param cdid The timeseries CDID.
     * @return The new timeseries.
     */
    public static TimeSeries addTimeseries(String cdid) {
        TimeSeries timeseries = new TimeSeries();
        timeseries.setCdid(cdid);
        addTimeseries(timeseries);
        return timeseries;
    }

    /**
     * Adds a new dataset. If the dataset is already present, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param dataset The dataset.
     */
    public static void addDataset(Dataset dataset) {
        if (datasets.contains(dataset)) {
            throw new IllegalArgumentException("Duplicate dataset: " + dataset);
        }
        datasets.add(dataset);
    }

    /**
     * Adds a new dataset. If the dataset is already present, an
     * {@link IllegalArgumentException} is thrown.
     *
     */
    public static void setDataset(TimeSeries timeseries, String datasetName) {

        Set<TimeSeries> dataset;
        if (datasetName != null) {
            dataset = oldDataset(datasetName);
            if (dataset == null) {
                throw new RuntimeException("There's no dataset called " + datasetName + " to add " + timeseries + " to.");
            }
        } else {
            dataset = (oldDataset("other") == null) ? addOldDataset("other") : oldDataset("other");
        }
        dataset.add(timeseries);
    }

    /**
     * Adds a new dataset. If the dataset is already present, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param name    The dataset title.
     * @param dataset The dataset.
     */
    public static void addOldDataset(String name, Set<TimeSeries> dataset) {
        if (oldDatasets.containsKey(toKey(name))) {
            throw new IllegalArgumentException("Duplicate dataset: " + name);
        }
        oldDatasets.put(StringUtils.lowerCase(name), dataset);
    }

    /**
     * Creates and adds a new dataset. If the dataset is already present, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param name The dataset title.
     * @return
     */
    public static Set<TimeSeries> addOldDataset(String name) {
        Set<TimeSeries> dataset = new HashSet<>();
        addOldDataset(name, dataset);
        return dataset;
    }

    public static void addMappedDataset(String name) {
        mappedDatasets.add(toKey(name));
    }

    public static Set<String> unmappedOldDatasets() {
        Set<String> result = new HashSet<>(oldDatasets.keySet());
        for (String mapped : mappedDatasets) {
            result.remove(toKey(mapped));
        }
        return result;
    }

    public static void addRelatedTimeseries(TimeSeries timeseries, List<TimeSeries> relatedTimeserieses) {
        relatedTimeseries.put(timeseries, relatedTimeserieses);
    }

    public static List<TimeSeries> relatedTimeseries(TimeSeries timeseries) {
        return relatedTimeseries.get(timeseries);
    }

    public static Map<TimeSeries, List<TimeSeries>> getRelatedTimeseries() {
        return relatedTimeseries;
    }

    /**
     * @return The total number of timeseries currently held.
     */
    public static int size() {
        return timeserieses.size();
    }

    /**
     * @return The total number of timeseries currently held.
     */
    public static int sizeOldDatasets() {
        Set<String> cdids = new HashSet<>();
        for (String dataset : oldDatasets.keySet()) {
            cdids.add(dataset);
        }
        return timeserieses.size();
    }

    /**
     * @return The total number of timeseries currently held.
     */
    public static int sizeOldDatasetsCount() {
        return oldDatasets.size();
    }

    /**
     * Standardises the given value.
     *
     * @param string The value - typically a CDID.
     * @return The value, trimmed and lowercased, or null if null was passed in.
     */
    private static String toKey(String string) {
        return StringUtils.lowerCase(StringUtils.trim(string));
    }

    @Override
    public Iterator<TimeSeries> iterator() {
        return new Iterator<TimeSeries>() {

            int index = 0;
            List<String> items = new ArrayList<>(timeserieses.keySet());

            @Override
            public boolean hasNext() {
                return index < items.size();
            }

            @Override
            public TimeSeries next() {
                return timeserieses.get(items.get(index++));
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
