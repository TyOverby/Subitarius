/*
 * TestingContextListener.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.parse;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.prealpha.dispatch.server.filter.BatchActionModule;
import com.subitarius.domain.DomainModule;
import com.subitarius.instance.server.InstanceServerModule;
import com.subitarius.instance.server.action.SubitariusActionModule;
import com.subitarius.util.http.HttpModule;

public class TestingContextListener extends GuiceServletContextListener {
	public TestingContextListener() {
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new JpaPersistModule("instance"),
				new InstanceServerModule(), new SubitariusActionModule(),
				new BatchActionModule(), new DomainModule(), new ParseModule(),
				new HttpModule());
	}
}
