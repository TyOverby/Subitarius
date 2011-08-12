/*
 * ExtempDbContextListener.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.prealpha.dispatch.server.DispatchServerModule;
import com.prealpha.dispatch.server.filter.BatchActionModule;
import com.prealpha.extempdb.domain.DomainModule;
import com.prealpha.extempdb.instance.server.action.ActionModule;
import com.prealpha.extempdb.instance.server.parse.ParseModule;
import com.prealpha.extempdb.util.http.HttpModule;

public class ExtempDbContextListener extends GuiceServletContextListener {
	public ExtempDbContextListener() {
	}

	@Override
	protected Injector getInjector() {
		return Guice
				.createInjector(new JpaPersistModule("instance"),
						new DispatchServerModule(), new BatchActionModule(),
						new ActionModule(), new DomainModule(),
						new ExtempDbServerModule(), new HttpModule(),
						new ParseModule());
	}
}
