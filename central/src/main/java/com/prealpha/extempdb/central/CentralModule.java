/*
 * CentralModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central;

import javax.servlet.http.HttpSession;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;
import com.prealpha.extempdb.domain.User;
import com.prealpha.extempdb.util.logging.Slf4jTypeListener;

public final class CentralModule extends ServletModule {
	static final String USER_ATTR = "user";

	public CentralModule() {
	}

	@Override
	protected void configureServlets() {
		filter("/*").through(PersistFilter.class);

		bind(SearcherServlet.class).in(Singleton.class);
		serve("/searcher").with(SearcherServlet.class);
		
		bind(AuthenticationServlet.class).in(Singleton.class);
		serve("/auth").with(AuthenticationServlet.class);

		bindListener(Matchers.any(), new Slf4jTypeListener());
	}

	@Provides
	@RequestScoped
	@Inject
	User getUser(HttpSession session) {
		return (User) session.getAttribute(USER_ATTR);
	}
}
