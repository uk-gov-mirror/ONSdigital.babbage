package com.github.onsdigital.api.data;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.bean.CdidRequest;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.util.ContentUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serves data files in xls or csv format
 */

/**
 * Provides the ability to request the json for one or more CDIDs.
 *
 * @author david
 *
 */
@Api
public class Cdid {

static Map<String, TimeSeries> cache = new ConcurrentHashMap<String, TimeSeries>();

	@POST
	public Map<String, TimeSeries> post(@Context HttpServletRequest request, @Context HttpServletResponse response, CdidRequest cdidRequest) throws IOException {
		System.out.println("Download request recieved" + cdidRequest);
		return processRequest(cdidRequest);
	}

	private Map<String, TimeSeries> processRequest(CdidRequest cdidRequest) throws IOException {
		return getTimeSeries(cdidRequest.cdids);
	}

	static Map<String, TimeSeries> getTimeSeries(List<String> cdids) throws IOException {
		Map<String, TimeSeries> result = new HashMap<>();

		// Start with cache hits:
		List<String> missing = new ArrayList<>();
		for (String cdid : cdids) {
			TimeSeries TimeSeries = cache.get(cdid.toUpperCase());
			if (TimeSeries != null) {
				System.out.println("Cache hit for : " + cdid);
				result.put(TimeSeries.getCdid(), TimeSeries);
			} else {
				System.out.println("Cache miss for : " + cdid);
				missing.add(cdid);
			}
		}

		// Load any missing items:
		if (missing.size() > 0) {
			List<Path> TimeSeriesPaths = findTimeSeries(missing);
			for (Path path : TimeSeriesPaths) {
				try (InputStream input = Files.newInputStream(path)) {
					System.out.println(path);
					TimeSeries TimeSeries = ContentUtil.deserialise(input, TimeSeries.class);
					result.put(TimeSeries.getCdid(), TimeSeries);
					cache.put(TimeSeries.getCdid(), TimeSeries);
				}
			}
		}

		return result;
	}

	/**
	 * Scans the taxonomy to find the requested TimeSeries.
	 *
	 * @param cdids
	 *            The list of CDIDs to find.
	 * @return A list of paths for the given CDIDs, if found.
	 * @throws IOException
	 */
	private static List<Path> findTimeSeries(final List<String> cdids) throws IOException {
		final List<Path> result = new ArrayList<>();

		Path taxonomy = FileSystems.getDefault().getPath(Configuration.CONTENT_SERVICE.getContentPath());

		/**
		 * Finds json files inside folders that match a cdid value.
		 */
		FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String filename = file.getFileName().toString();
				String extension = FilenameUtils.getExtension(filename);
				String parent = file.getParent().getFileName().toString();

				if (StringUtils.equalsIgnoreCase("json", extension)) {
					for (String cdid : cdids) {
						if (StringUtils.equalsIgnoreCase(cdid, parent)) {
							result.add(file);
						}
					}
				}

				return FileVisitResult.CONTINUE;
			}
		};
		Files.walkFileTree(taxonomy, fv);

		return result;
	}

}
