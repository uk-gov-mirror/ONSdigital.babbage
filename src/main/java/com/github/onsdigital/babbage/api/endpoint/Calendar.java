package com.github.onsdigital.babbage.api.endpoint;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.filter.PublishedFilter;
import com.github.onsdigital.babbage.search.model.filter.UpcomingFilter;
import com.github.onsdigital.babbage.util.ThreadContext;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Serves ical format release calendar starting from last three months. Used for subscribing ons release calendar
 */

@Api
public class Calendar {

    @GET
    public void get(@Context HttpServletRequest httpServletRequest,
                    @Context HttpServletResponse httpServletResponse)
            throws IOException, ValidationException {

        httpServletResponse.setContentType("text/calendar");
        httpServletResponse.setCharacterEncoding("UTF8");
        httpServletResponse.setHeader("Content-Disposition",
                "attachment; filename=releases.ics");

        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        PropertyList properties = calendar.getProperties();
        properties.add(new ProdId("-//Office for National Statistics//NONSGML//EN"));
        properties.add(Version.VERSION_2_0);
        properties.add(CalScale.GREGORIAN);
        addReleaseEvents(calendar, httpServletRequest);
        try {
            calendar.validate();
        } catch (ValidationException e) {
            System.err.println("Validation failed");
            e.printStackTrace();
        }
        new CalendarOutputter(false).output(calendar, httpServletResponse.getOutputStream());
    }


    public void addReleaseEvents(net.fortuna.ical4j.model.Calendar calendar, HttpServletRequest request) throws IOException {
        Map<String, SearchResult> results = query(request);
        List<Map<String, Object>> releases = results.get("result").getResults();
        for (Map<String, Object> release : releases) {
            calendar.getComponents().add(toEvent(release));
        }
    }

    private VEvent toEvent(Map<String, Object> release) {
        Map<String, Object> description = ((Map<String, Object>) release.get("description"));
        if (description == null) {
            System.out.println("!!!!Warning: Release with no description found, uri: " + release.get("uri"));
            return null;
        }

        try {
            String title = (String) description.get("title") + description.get("edition");
            Date date = new SimpleDateFormat(Configuration.CONTENT_SERVICE.getDefaultContentDatePattern()).parse((String) description.get("releaseDate"));
            net.fortuna.ical4j.model.Date eventDate = new net.fortuna.ical4j.model.DateTime(date.getTime());
            VEvent event = new VEvent(eventDate, eventDate, title);
            event.getProperties().add(new Uid(getUid(release)));
            event.getProperties().add(getStatus(description));
            event.getProperties().add(new Description((String) description.get("summary")));
            return event;
        } catch (Exception e) {
            System.err.println("!!!!Warning: Failed creating calendar even for release, uri: " + release.get("uri"));
            e.printStackTrace();
            return null;
        }
    }

    private String getUid(Map<String, Object> release) {
        String domain_name = (String) ThreadContext.getData("domain_name");
        String uri = (String) release.get("uri");
        return uri + "@" + domain_name;
    }

    private Status getStatus(Map<String, Object> releaseDescriptiion) {
        Boolean cancelled = releaseDescriptiion.get("cancelled") == Boolean.TRUE;
        if (cancelled) {
            return Status.VEVENT_CANCELLED;
        }

        Boolean finalised = releaseDescriptiion.get("finalised") == Boolean.TRUE;
        if (finalised) {
            return Status.VEVENT_CONFIRMED;
        } else {
            return Status.VEVENT_TENTATIVE;
        }
    }


    private Date getThreeMonthsAgo() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(java.util.Calendar.MONTH, -3);
        return cal.getTime();
    }

    private boolean isUpcoming(HttpServletRequest request) {
        return "upcoming".equalsIgnoreCase(request.getParameter("view"));
    }

    private Map<String, SearchResult> query(HttpServletRequest request) {
        final SearchParam searchParam = SearchParamFactory.getInstance(request, SortBy.first_letter, Collections.singleton(QueryType.SEARCH));
        searchParam.addDocTypes(ContentType.release);
        if (isUpcoming(request)) {
            searchParam.addFilter(new UpcomingFilter());
        } else {
            searchParam.addFilter(new PublishedFilter());
        }
        searchParam.setSize(10000);
        Map<String, SearchResult> results = null;
        try {
            results = SearchUtils.search(searchParam);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return results;
    }


}
