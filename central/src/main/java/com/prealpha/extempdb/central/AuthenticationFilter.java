/*
 * AuthenticationFilter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.domain.Team;
import com.prealpha.extempdb.util.logging.InjectLogger;

/**
 * A simple filter to check that a requester has authenticated through
 * {@link AuthenticationServlet} before accessing secure resources. The filter
 * simply checks that a logged in team exists and that the team license has not
 * expired. If either of these tests fails, status code 401 (unauthorized) is
 * sent as a response instead of passing the request on.
 * 
 * @author Meyer Kizner
 * 
 */
final class AuthenticationFilter implements Filter {
	@InjectLogger
	private Logger log;

	private final Provider<Team> teamProvider;

	@Inject
	private AuthenticationFilter(Provider<Team> teamProvider) {
		this.teamProvider = teamProvider;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Team team = teamProvider.get();
		if (team != null && !team.isExpired()) {
			log.debug("access granted to team {}", team);
			chain.doFilter(request, response);
		} else {
			log.info("access denied to team {}", team);
			((HttpServletResponse) response)
					.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	@Override
	public void destroy() {
	}
}
