package com.github.onsdigital.babbage.util;

import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bren on 08/09/15.
 */
public class SearchRequestUtil {

    /**
     * Extract the page number from a request - for paged results.
     *
     * @param request
     * @return
     */
    public static int extractPage(HttpServletRequest request) {
        String page = request.getParameter("page");

        if (StringUtils.isEmpty(page)) {
            return 1;
        }
        try {
            int pageNumber = Integer.parseInt(page);
            if (pageNumber < 1) {
                throw new ResourceNotFoundException();
            }
            return pageNumber;
        } catch (NumberFormatException e) {
            return 1;
        }

    }

}
