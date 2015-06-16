package com.github.onsdigital.generator.data;

import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.generator.ContentNode;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Handles the data CSVs under the {@value #resourceName} folder.
 * <p>
 * This class and its members are package private (default visibility) because
 * the API doesn't need to be exposed to the rest of the application.
 * 
 * @author david
 *
 */
public class DatasetMappingsCSV {

	static final String resourceName = "/Taxonomy map - old datasets.xlsx";

	static String name = "Name";
	static String link = "Link";
	static String theme = "Theme";
	static String level2 = "Level 2";
	static String level3 = "Level 3";
	/**
	 * This is a List rather than a Set because several datasets map to the same
	 * folders.
	 */
	public static Map<String, ContentNode> mappedFolders = new TreeMap<>();

	public static void parse() throws IOException {

		// Read in the data:
		Csv csv;
		try {
			// Now apply the data from the manually-prepared CSV:
			URL resource = DatasetMappingsCSV.class.getResource(resourceName);
			Path manuallyEditedCsv = Paths.get(resource.toURI());
			csv = read(manuallyEditedCsv);
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}

		// Process the rows
		for (Map<String, String> row : csv) {

			// Get the dataset details:
			String datasetName = StringUtils.trim(row.get(name));
			Set<TimeSeries> dataset = Data.oldDataset(datasetName);
			if (dataset == null) {
				throw new RuntimeException("Unable to find a dataset for title '" + datasetName);
			}
			Data.addMappedDataset(datasetName);

			// Find the folder this dataset is associated with:
			ContentNode folder = Data.getFolder(row.get(theme), row.get(level2), row.get(level3));
			if (folder.getChildren().size() > 0) {
				throw new RuntimeException("It looks like folder " + folder + " is not a T3.");
			}

			// Set timeseries URIs as necessary.
			// NB if a timeseries is already specifically associated with a
			// different folder in the taxonomy, we don't want to change that
			// URI.
			for (TimeSeries timeseries : dataset) {
				if (timeseries.getUri() == null) {
					timeseries.setUri(AlphaContentCSV.toUri(folder, timeseries));
				}
			}

			folder.oldDataset.add(dataset);
			mappedFolders.put(datasetName, folder);
			System.out.println(" - Dataset '" + datasetName + "' mapped to " + folder.path());
		}
		System.out.println("Total: " + mappedFolders.size());
	}

	private static Csv read(Path file) throws IOException {
		Csv csv = new Csv(file);
		csv.read(0);
		String[] headings = { name, link, theme, level2, level3 };
		csv.setHeadings(headings);
		return csv;
	}

}
