/*
 * CentralContextListener.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.prealpha.extempdb.central.search.SearchModule;
import com.prealpha.extempdb.util.http.HttpModule;

public final class CentralContextListener extends GuiceServletContextListener {
	public CentralContextListener() {
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new JpaPersistModule("central"),
				new CentralModule(), new SearchModule(), new HttpModule());
	}
}
