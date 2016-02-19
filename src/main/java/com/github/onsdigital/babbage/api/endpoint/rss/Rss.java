package com.github.onsdigital.babbage.api.endpoint.rss;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.endpoint.rss.builder.SyndFeedBuilder;
import com.github.onsdigital.babbage.api.endpoint.rss.service.RssService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

/**
 * API endpoint for ONS RSS Feed.
 */

@Api
public class Rss {

	private static final String MIME_TYPE = "application/rss+xml; charset=ISO-8859-1";

	private final RssService rssService = RssService.getInstance();
	private RssSearchFilter.Builder filterBuilder = new RssSearchFilter.Builder();

	/**
	 * Get an RSS feed for the latest releases matching the search criteria specified in the request parameters.
	 */
	@GET
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		RssSearchFilter filter = filterBuilder.build(request);
		SyndFeedBuilder.writeFeedToResponse(rssService.getFeed(filter), response);
		response.setContentType(MIME_TYPE);
	}
}
