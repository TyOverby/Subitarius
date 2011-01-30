/*
 * ExtempDbContextListener.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.prealpha.dispatch.server.DispatchServerModule;
import com.prealpha.dispatch.server.filter.BatchActionModule;
import com.prealpha.extempdb.server.action.ActionModule;
import com.prealpha.extempdb.server.http.HttpModule;
import com.prealpha.extempdb.server.parse.ParseModule;
import com.prealpha.extempdb.server.search.SearchModule;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.UnitOfWork;

public class ExtempDbContextListener extends GuiceServletContextListener {
	public ExtempDbContextListener() {
	}

	@Override
	protected Injector getInjector() {
		Module persistenceModule = PersistenceService.usingJpa()
				.across(UnitOfWork.REQUEST).buildModule();

		return Guice.createInjector(persistenceModule,
				new DispatchServerModule(), new BatchActionModule(),
				new ActionModule(), new ExtempDbServerModule(),
				new SearchModule(), new ParseModule(), new HttpModule());
	}
}
