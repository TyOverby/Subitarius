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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.domain.Team;
import com.prealpha.extempdb.domain.User;

/**
 * A simple filter to check that a requester has authenticated through
 * {@link AuthenticationServlet} before accessing secure resources. The filter
 * simply checks that a logged in user exists and that the team license for that
 * user has not expired. If either of these tests fails, status code 401
 * (unauthorized) is sent as a response instead of passing the request on.
 * 
 * @author Meyer Kizner
 * 
 */
final class AuthenticationFilter implements Filter {
	private final Provider<User> userProvider;

	@Inject
	private AuthenticationFilter(Provider<User> userProvider) {
		this.userProvider = userProvider;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		User user = userProvider.get();
		if (user != null) {
			Team team = user.getTeam();
			if (!team.isExpired()) {
				chain.doFilter(request, response);
				return;
			}
		}

		((HttpServletResponse) response)
				.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Override
	public void destroy() {
	}
}
