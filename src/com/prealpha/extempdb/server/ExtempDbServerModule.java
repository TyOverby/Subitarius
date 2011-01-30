/*
 * ExtempDbServerModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server;

import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.ServletModule;
import com.wideplay.warp.persist.PersistenceFilter;
import com.wideplay.warp.persist.jpa.JpaUnit;

public class ExtempDbServerModule extends ServletModule {
	public ExtempDbServerModule() {
	}

	@Override
	protected void configureServlets() {
		bind(GuiceServiceServlet.class).in(Singleton.class);
		serve("/GWT.rpc").with(GuiceServiceServlet.class);

		bind(SearcherServlet.class).in(Singleton.class);
		serve("/searcher").with(SearcherServlet.class);

		bindListener(Matchers.any(), new Slf4jTypeListener());

		filter("/*").through(PersistenceFilter.class);
		bindConstant().annotatedWith(JpaUnit.class).to("extempdb");
	}
}
