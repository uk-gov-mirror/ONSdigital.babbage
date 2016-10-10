package com.github.onsdigital.babbage.search.helpers.dates;

import com.github.onsdigital.babbage.error.ValidationError;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Simple POJO for holding search dates. Provides a convenient way to pass 2 date objects around and validating they
 * are valid and make sense.
 */
public class PublishDates {

    static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yy");

    public static final String UPDATED_TODAY = "today";
    public static final String UPDATED_WITHIN_A_WEEK = "week";
    public static final String UPDATED_WITHIN_A_MONTH = "month";
    public static final int ONE_DAY_BEFORE = 1;
    public static final int SEVEN_DAYS_BEFORE = 7;
    public static final int THIRTY_DAYS_BEFORE = 30;

    public static final String PUBLISHED_FROM = "publishedFrom";
    public static final String PUBLISHED_BEFORE = "publishedBefore";
    public static final String DATE_INVALID_MSG = "Date was invalid";

    public static final ValidationError PUBLISHED_FROM_INVALID =
            new ValidationError(PUBLISHED_FROM, "Invalid date.");

    public static final ValidationError PUBLISHED_BEFORE_INVALID =
            new ValidationError(PUBLISHED_BEFORE, "Date was invalid.");

    public static final ValidationError PUBLISHED_FROM_DATE_IN_FUTURE_ERROR =
            new ValidationError(PUBLISHED_FROM, "Published after cannot be in the future.");

    public static final ValidationError PUBLISHED_FROM_AFTER_END_DATE_ERROR =
            new ValidationError(PUBLISHED_FROM, "Published After begins after Published Before.");

    private Date publishedFrom = null;
    private Date publishedTo = null;

    private static Date daysBefore(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1 * days);
        return cal.getTime();
    }

    public static PublishDates updatedWithinPeriod(String period) {
        switch (period) {
            case UPDATED_TODAY:
                return new PublishDates(daysBefore(ONE_DAY_BEFORE), null);
            case UPDATED_WITHIN_A_WEEK:
                return new PublishDates(daysBefore(SEVEN_DAYS_BEFORE), null);
            case UPDATED_WITHIN_A_MONTH:
                return new PublishDates(daysBefore(THIRTY_DAYS_BEFORE), null);
            default:
                return new PublishDates();
        }
    }

    /**
     * Create a {@link PublishDates} object from the date string provided.
     *
     * @throws PublishDatesException if the date string parameters are invalid.
     */
    public static PublishDates publishedDates(String publishedAfter, String publishedBefore,
                                              boolean allowFutureAfterDate) throws PublishDatesException {
        List<ValidationError> validationErrors = new ArrayList<>();
        Date publishedFromDate;
        Date publishedToDate;
        try {
            publishedFromDate = formatDate(publishedAfter);
        } catch (ParseException pEx) {
            validationErrors.add(PUBLISHED_FROM_INVALID);
            publishedFromDate = null;
        }

        try {
            publishedToDate = formatDate(publishedBefore);
        } catch (ParseException pEx) {
            validationErrors.add(PUBLISHED_BEFORE_INVALID);
            publishedToDate = null;
        }

        if (!validateDateRange(publishedFromDate, publishedToDate, validationErrors, allowFutureAfterDate)
                .isEmpty()) {
            throw new PublishDatesException(validationErrors);
        }
        return new PublishDates(publishedFromDate, publishedToDate);
    }

    /**
     * Verify the published from date is not in the future and that published from is not after published before.
     */
    private static List<ValidationError> validateDateRange(Date from, Date to, List<ValidationError> validationErrors,
                                                           boolean allowFutureAfterDate) {
        if (!allowFutureAfterDate) {
            if (from != null && from.after(Calendar.getInstance().getTime())) {
                validationErrors.add(PUBLISHED_FROM_DATE_IN_FUTURE_ERROR);
            }
        }

        // Publish after value in date range is later than Published before.
        if (from != null && to != null) {
            if (from.after(to)) {
                validationErrors.add(PUBLISHED_FROM_AFTER_END_DATE_ERROR);
            }
        }
        return validationErrors;
    }

    /**
     * @return a {@link PublishDates} with no values - i.e. any date.
     */
    public static PublishDates publishedAnyTime() {
        return new PublishDates();
    }

    /**
     * Create a {@link PublishDates} object from the dates provided.
     *
     * @throws PublishDatesException the date value provided were invalid.
     */
    public static PublishDates publishedDates(Date publishedFrom, Date publishedTo) throws PublishDatesException {
        List<ValidationError> validationErrors = validateDateRange(publishedFrom, publishedTo, new ArrayList<>(), false);
        if (!validationErrors.isEmpty()) {
            throw new PublishDatesException(validationErrors);
        }
        return new PublishDates(publishedFrom, publishedTo);
    }

    private static Date formatDate(String input) throws ParseException, IllegalArgumentException {
        if (StringUtils.isEmpty(input)) {
            return null;
        }
        SDF.setLenient(false);
        return SDF.parse(input);
    }

    private PublishDates() {
        this.publishedTo = null;
        this.publishedFrom = null;
    }

    private PublishDates(Date from, Date to) {
        this.publishedFrom = from;
        this.publishedTo = to;
    }

    public Date publishedFrom() {
        return publishedFrom;
    }

    public Date publishedTo() {
        return publishedTo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        PublishDates dates = (PublishDates) obj;

        if (this.publishedFrom != null && !this.publishedFrom.equals(dates.publishedFrom)) {
            return false;
        }
        if (this.publishedFrom == null && dates.publishedFrom != null) {
            return false;
        }
        if (this.publishedTo != null && !this.publishedTo.equals(dates.publishedTo)) {
            return false;
        }
        if (this.publishedTo == null && dates.publishedTo != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
