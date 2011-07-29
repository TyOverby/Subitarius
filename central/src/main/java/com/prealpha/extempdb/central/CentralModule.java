/*
 * CentralModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central;

import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.ServletModule;
import com.prealpha.extempdb.central.search.SearcherServlet;
import com.prealpha.extempdb.util.logging.Slf4jTypeListener;

public final class CentralModule extends ServletModule {
	public CentralModule() {
	}

	@Override
	protected void configureServlets() {
		filter("/*").through(PersistFilter.class);

		bind(SearcherServlet.class).in(Singleton.class);
		serve("/searcher").with(SearcherServlet.class);

		bindListener(Matchers.any(), new Slf4jTypeListener());
	}
}
