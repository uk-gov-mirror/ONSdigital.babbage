package com.github.onsdigital.babbage.api.endpoint;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.search.helpers.ONSSearchResponse;
import com.github.onsdigital.babbage.search.helpers.SearchHelper;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.util.ThreadContext;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import org.elasticsearch.index.query.BoolQueryBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.onsQuery;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;


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
        addReleaseEvents(calendar);
        try {
            calendar.validate();
        } catch (ValidationException e) {
            error().exception(e).log("get calendar validation error");
        }
        new CalendarOutputter(false).output(calendar, httpServletResponse.getOutputStream());
    }


    public void addReleaseEvents(net.fortuna.ical4j.model.Calendar calendar) throws IOException {
        BoolQueryBuilder releaseQuery = boolQuery()
                .filter(rangeQuery(Field.releaseDate.fieldName())
                        .from(getThreeMonthsAgo()));
        ONSSearchResponse searchResponse = SearchHelper
                .search(onsQuery(releaseQuery)
                        .fetchFields(Field.title, Field.edition, Field.releaseDate, Field.summary)
                        .types(ContentType.release).size(10000));
        List<Map<String, Object>> releases = searchResponse.getResult().getResults();
        for (Map<String, Object> release : releases) {
            calendar.getComponents().add(toEvent(release));
        }
    }

    private VEvent toEvent(Map<String, Object> release) {
        Map<String, Object> description = ((Map<String, Object>) release.get("description"));
        if (description == null) {
            Object uri = release.get("uri");
            String uriStr = uri != null ? uri.toString() : "";
            info().data("uri", uriStr).log("release with no description found");

            return null;
        }

        try {
            String title = (String) description.get("title") + description.get("edition");
            Date date = appConfig().contentAPI()
                    .defaultContentDateFormat()
                    .parse((String) description.get("releaseDate"));

            net.fortuna.ical4j.model.Date eventDate = new net.fortuna.ical4j.model.DateTime(date.getTime());
            VEvent event = new VEvent(eventDate, eventDate, title);
            event.getProperties().add(new Uid(getUid(release)));
            event.getProperties().add(getStatus(description));
            event.getProperties().add(new Description((String) description.get("summary")));
            return event;
        } catch (Exception e) {
            Object uri = release.get("uri");
            error().data("uri", uri != null ? uri.toString() : null).log("failed creating calendar even for release");
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


    public Date getThreeMonthsAgo() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(java.util.Calendar.MONTH, -3);
        return cal.getTime();
    }


}
