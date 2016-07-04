package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.error.ValidationError;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDates;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDatesException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.FROM_DAY_PARAM;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.FROM_MONTH_PARAM;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.FROM_YEAR_PARAM;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.SDF;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.TO_DAY_PARAM;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.TO_MONTH_PARAM;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.TO_YEAR_PARAM;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.UPDATED_PARAM;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractPublishDates;
import static com.github.onsdigital.babbage.search.helpers.dates.PublishDates.UPDATED_TODAY;
import static com.github.onsdigital.babbage.search.helpers.dates.PublishDates.UPDATED_WITHIN_A_MONTH;
import static com.github.onsdigital.babbage.search.helpers.dates.PublishDates.UPDATED_WITHIN_A_WEEK;
import static com.github.onsdigital.babbage.search.helpers.dates.PublishDates.publishedDates;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

public class SearchRequestHelperTest {

    @Mock
    HttpServletRequest mockRequest;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)}
     */
    @Test
    public void shouldCreatePublishDateForValidFromDateIn_DD_MM_YY_format() throws Exception {
        setFrom("1", "1", "16");
        PublishDates expected = publishedDates(toDate("01/01/2016"), null);
        assertThat("PublishDates were not as expected.", expected, equalTo(extractPublishDates(mockRequest)));
    }

    /**
     * Test {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)}
     */
    @Test
    public void shouldCreatePublishDateForValidToDateIn_DD_MM_YY_format() throws Exception {
        setTo("1", "1", "16");
        PublishDates expected = publishedDates(null, toDate("01/01/2016"));
        assertThat("PublishDates were not as expected.", expected, equalTo(extractPublishDates(mockRequest)));
    }

    /**
     * Test {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)}
     */
    @Test
    public void shouldCreatePublishDateFromValidDatesIn_DD_MM_YY_format() throws Exception {
        setFrom("1", "1", "15");
        setTo("1", "1", "16");
        PublishDates expected = publishedDates(toDate("01/01/2015"), toDate("01/01/2016"));
        assertThat("PublishDates were not as expected.", expected, equalTo(extractPublishDates(mockRequest)));
    }

    /**
     * Test {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)}
     */
    @Test
    public void shouldCreatePublishDateFromValidDatesIn_DD_MM_YYYY_format() throws Exception {
        setFrom("1", "1", "2015");
        setTo("1", "1", "2016");
        PublishDates expected = publishedDates(toDate("01/01/2015"), toDate("01/01/2016"));
        assertThat("PublishDates were not as expected.", expected, equalTo(extractPublishDates(mockRequest)));
    }

    /**
     * Test {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)}
     */
    @Test(expected = PublishDatesException.class)
    public void shouldRejectFromDateWithInvalidDayValue() throws Exception {
        setFrom("100", "01", "2016");
        try {
            extractPublishDates(mockRequest);
        } catch (PublishDatesException ex) {
            assertThat("Errors not as expected", ex.getErrors(), equalTo(errorsList(PublishDates.PUBLISHED_FROM_INVALID)));
            throw ex;
        }
    }

    /**
     * Test {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)}
     */
    @Test(expected = PublishDatesException.class)
    public void shouldRejectFromDateWithInvalidMonthValue() throws Exception {
        setFrom("1", "27", "2016");
        try {
            extractPublishDates(mockRequest);
        } catch (PublishDatesException ex) {
            assertThat("Errors not as expected", ex.getErrors(), equalTo(errorsList(PublishDates.PUBLISHED_FROM_INVALID)));
            throw ex;
        }
    }

    /**
     * Test {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)}
     */
    @Test(expected = PublishDatesException.class)
    public void shouldRejectFromDateWithInvalidYearValue() throws Exception {
        setFrom("1", "2", "-1");
        try {
            extractPublishDates(mockRequest);
        } catch (PublishDatesException ex) {
            assertThat("Errors not as expected", ex.getErrors(), equalTo(errorsList(PublishDates.PUBLISHED_FROM_INVALID)));
            throw ex;
        }
    }

    /**
     * Test {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)}
     */
    @Test(expected = PublishDatesException.class)
    public void shouldRejectFromDateDayWithNonNumericValue() throws Exception {
        setFrom("a", "01", "2016");
        try {
            extractPublishDates(mockRequest);
        } catch (PublishDatesException ex) {
            assertThat("Errors not as expected", ex.getErrors(), equalTo(errorsList(PublishDates.PUBLISHED_FROM_INVALID)));
            throw ex;
        }
    }

    /**
     * Test {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)}
     */
    @Test(expected = PublishDatesException.class)
    public void shouldRejectFromMonthWithNonNumericValue() throws Exception {
        setFrom("1", "a", "2016");
        try {
            extractPublishDates(mockRequest);
        } catch (PublishDatesException ex) {
            assertThat("Errors not as expected", ex.getErrors(), equalTo(errorsList(PublishDates.PUBLISHED_FROM_INVALID)));
            throw ex;
        }
    }

    /**
     * Test {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)}
     */
    @Test(expected = PublishDatesException.class)
    public void shouldRejectFromYearWithNonNumericValue() throws Exception {
        setFrom("1", "1", "a");
        try {
            extractPublishDates(mockRequest);
        } catch (PublishDatesException ex) {
            assertThat("Errors not as expected", ex.getErrors(), equalTo(errorsList(PublishDates.PUBLISHED_FROM_INVALID)));
            throw ex;
        }
    }

    /**
     * Test {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)}
     */
    @Test(expected = PublishDatesException.class)
    public void shouldRejectWhenPublishedAfterIsLaterThatPublishedBefore() throws Exception {
        setFrom("1", "1", "2016");
        setTo("1", "1", "2015");

        try {
            extractPublishDates(mockRequest);
        } catch (PublishDatesException ex) {
            assertThat("Errors not as expected", ex.getErrors(), equalTo(errorsList(
                    PublishDates.PUBLISHED_FROM_AFTER_END_DATE_ERROR)));
            throw ex;
        }
    }

    /**
     * Test verifies that {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)} returns the correct
     * published from date when 'updated=today' is supplied.
     */
    @Test
    public void shouldReturnPublishDateWithinADayOfCurrentDate() throws Exception {
        when(mockRequest.getParameter(UPDATED_PARAM))
                .thenReturn(UPDATED_TODAY);
        verifyDateUpdated(extractPublishDates(mockRequest), 1);
    }

    /**
     * Test verifies that {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)} returns the correct
     * published from date when 'updated=week' is supplied.
     */
    @Test
    public void shouldReturnPublishDateWithinAWeekOfCurrentDate() throws Exception {
        when(mockRequest.getParameter(UPDATED_PARAM))
                .thenReturn(UPDATED_WITHIN_A_WEEK);
        verifyDateUpdated(extractPublishDates(mockRequest), 7);
    }

    /**
     * Test verifies that {@link SearchRequestHelper#extractPublishDates(HttpServletRequest)} returns the correct
     * published from date when 'updated=month' is supplied.
     */
    @Test
    public void shouldReturnPublishDateWithinAMonthOfCurrentDate() throws Exception {
        when(mockRequest.getParameter(UPDATED_PARAM))
                .thenReturn(UPDATED_WITHIN_A_MONTH);
        verifyDateUpdated(extractPublishDates(mockRequest), 30);
    }

    private void verifyDateUpdated(PublishDates result, int range) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1 * range);
        assertThat("From date is incorrect.", result.publishedFrom(), equalTo(cal.getTime()));
    }

    private void setFrom(String day, String month, String year) {
        when(mockRequest.getParameter(FROM_DAY_PARAM))
                .thenReturn(day);
        when(mockRequest.getParameter(FROM_MONTH_PARAM))
                .thenReturn(month);
        when(mockRequest.getParameter(FROM_YEAR_PARAM))
                .thenReturn(year);
    }

    private void setTo(String day, String month, String year) {
        when(mockRequest.getParameter(TO_DAY_PARAM))
                .thenReturn(day);
        when(mockRequest.getParameter(TO_MONTH_PARAM))
                .thenReturn(month);
        when(mockRequest.getParameter(TO_YEAR_PARAM))
                .thenReturn(year);
    }

    private Date toDate(String value) throws Exception {
        return SDF.parse(value);
    }

    private List<ValidationError> errorsList(ValidationError... errors) {
        return Arrays.asList(errors);
    }
}
