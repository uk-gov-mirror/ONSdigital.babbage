package com.github.onsdigital.babbage.search.helpers.dates;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by guidof on 30/03/17.
 */
public class PublishDatesTest {
    @Test
    public void daysBefore() throws Exception {
        final Calendar calendar = getToday();
        calendar.add(Calendar.DAY_OF_WEEK, -2);
        Date twoDaysAgo = calendar.getTime();
        final Date actual = PublishDates.daysBefore(2);
        assertEquals(twoDaysAgo, actual);
    }

    private Calendar getToday() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @Test
    public void updatedWithinPeriodToday() {
        final Calendar today = getToday();
        today.add(Calendar.DAY_OF_WEEK, -1);
        final Date expected = today.getTime();
        final PublishDates publishDates = PublishDates.updatedWithinPeriod(PublishDates.UPDATED_YESTERDAY);
        assertEquals(expected, publishDates.publishedFrom());
    }


    @Test
    public void updatedWithinPeriod2DaysPrevious() {
        final Calendar today = getToday();
        today.add(Calendar.DAY_OF_WEEK, -2);
        final Date expected = today.getTime();
        final PublishDates publishDates = PublishDates.updatedWithinPeriod(PublishDates.UPDATED_TWO_DAYS_AGO);
        assertEquals(expected, publishDates.publishedFrom());
    }

}