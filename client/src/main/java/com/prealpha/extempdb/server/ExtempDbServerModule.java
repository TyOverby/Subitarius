/*
 * ExtempDbServerModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server;

import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.ServletModule;
import com.prealpha.extempdb.util.logging.Slf4jTypeListener;

public class ExtempDbServerModule extends ServletModule {
	public ExtempDbServerModule() {
	}

	@Override
	protected void configureServlets() {
		filter("/*").through(PersistFilter.class);

		bind(GuiceServiceServlet.class).in(Singleton.class);
		serve("/GWT.rpc").with(GuiceServiceServlet.class);

		bindListener(Matchers.any(), new Slf4jTypeListener());
	}
}
