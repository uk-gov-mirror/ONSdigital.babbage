package com.github.onsdigital.api;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.util.NavigationUtil;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.StringReader;

/**
 * Navigation end point that returns taxonomy leves
 * 
 * @author Bren
 */

@Api
public class Navigation {

	@GET
	public static Object get(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {

		response.setCharacterEncoding("UTF8");
		response.setContentType("application/json");
		IOUtils.copy(new StringReader(NavigationUtil.getNavigation().toJson()), response.getOutputStream());
		return null;

	}

}
