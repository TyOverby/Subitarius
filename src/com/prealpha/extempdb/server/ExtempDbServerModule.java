/*
 * ExtempDbServerModule.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server;

import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.ServletModule;

public class ExtempDbServerModule extends ServletModule {
	@Override
	protected void configureServlets() {
		bind(GuiceServiceServlet.class).in(Singleton.class);
		serve("/GWT.rpc").with(GuiceServiceServlet.class);

		bind(SearcherServlet.class).in(Singleton.class);
		serve("/searcher").with(SearcherServlet.class);

		bindListener(Matchers.any(), new Slf4jTypeListener());
	}
}
