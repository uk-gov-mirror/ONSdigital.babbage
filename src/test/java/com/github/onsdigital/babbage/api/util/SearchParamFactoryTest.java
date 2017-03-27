package com.github.onsdigital.babbage.api.util;

import com.github.onsdigital.babbage.search.input.SortBy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by guidof on 24/03/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchParamFactoryTest {
    @Mock
    HttpServletRequest request;

    @Test
    public void getInstance() throws Exception {
        when(request.getParameter(eq("query"))).thenReturn("bananas");
        when(request.getParameter(eq("size"))).thenReturn("66");
        when(request.getParameter(eq("page"))).thenReturn("99");
        when(request.getParameter(eq("sortBy"))).thenReturn("title");
        final SearchParam instance = SearchParamFactory.getInstance(request, SortBy.release_date_asc);
        assertEquals("bananas", instance.getSearchTerm());
        assertEquals(Integer.valueOf(66), instance.getSize());
        assertEquals(Integer.valueOf(99), instance.getPage());
        assertEquals(SortBy.title, instance.getSortBy());

    }

    @Test
    public void getInstanceNoParams() throws Exception {
        when(request.getParameter(eq("query"))).thenReturn(null);
        when(request.getParameter(eq("size"))).thenReturn(null);
        when(request.getParameter(eq("page"))).thenReturn(null);
        when(request.getParameter(eq("sortBy"))).thenReturn(null);
        final SearchParam instance = SearchParamFactory.getInstance(request, SortBy.release_date_asc);
        assertNull(instance.getSearchTerm());
        assertEquals(10, (int) instance.getSize());
        assertEquals(1, (int) instance.getPage());
        assertEquals(SortBy.release_date_asc, instance.getSortBy());

    }

}