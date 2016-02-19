package com.github.onsdigital.babbage.response;

import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class BabbageRssResponse extends BabbageResponse {

	private static final String MIME_TYPE = "application/rss+xml; charset=ISO-8859-1";

	private SyndFeed rssFeed;

	public static class Builder {

		public BabbageRssResponse build(SyndFeed feed) {
			requireNonNull(feed, "BabbageRssResponse: SyndFeed is required and cannot be null.");
			return new BabbageRssResponse(feed);
		}
	}

	private BabbageRssResponse(SyndFeed rssFeed) {
		super(MIME_TYPE);
		this.rssFeed = rssFeed;
	}

	@Override
	public void apply(HttpServletRequest request, HttpServletResponse response) throws IOException {
		super.apply(request, response);
		try {
			new SyndFeedOutput().output(this.rssFeed, response.getWriter());
		} catch (FeedException ex){
			// TODO fix this.
			ex.printStackTrace();
		}
	}
}
