package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.api.endpoint.rss.service.RssService;
import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.ListRequestHandler;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.github.onsdigital.babbage.search.model.filter.UpcomingFilter;
import com.github.onsdigital.babbage.search.model.filter.PublishedFilter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;


/**
 * Created by bren on 22/09/15.
 */
public class ReleaseCalendar extends BaseRequestHandler implements ListRequestHandler {

	private static final String REQUEST_TYPE = "releasecalendar";
	private static final String VIEW_PARAM = "view";
	private static final String UPCOMING_VIEW = "upcoming";

	private static RssService rssService = RssService.getInstance();
	private static SearchService searchService = SearchService.get();

	@Override
	public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
		if (rssService.isRssRequest(request)) {
			return rssService.getReleaseCalendarFeedResponse(request, query(request));
		} else {
			return searchService.getBabbageResponseListPage(getClass().getSimpleName(), query(request));
		}
	}

	@Override
	public BabbageResponse getData(String uri, HttpServletRequest request) throws IOException, ContentReadException, URISyntaxException {
		return searchService.listJson(getClass().getSimpleName(), query(request));
	}

	@Override
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	private boolean isUpcoming(HttpServletRequest request) {
		return UPCOMING_VIEW.equalsIgnoreCase(request.getParameter(VIEW_PARAM));
	}

	private SearchParam query(HttpServletRequest request) {
		final SearchParam searchParam = SearchParamFactory.getInstance(request, SortBy.first_letter, Collections.singleton(QueryType.SEARCH));
		searchParam.addDocTypes(ContentType.release);
		if (isUpcoming(request)) {
			searchParam.addFilter(new UpcomingFilter());
		} else {
			searchParam.addFilter(new PublishedFilter());
		}

		return searchParam;
	}
}
