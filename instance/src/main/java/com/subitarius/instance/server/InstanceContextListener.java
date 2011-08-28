/*
 * InstanceContextListener.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.prealpha.dispatch.server.DispatchServerModule;
import com.prealpha.dispatch.server.filter.BatchActionModule;
import com.subitarius.domain.DomainModule;
import com.subitarius.instance.server.action.ActionModule;
import com.subitarius.instance.server.parse.ParseModule;
import com.subitarius.util.http.HttpModule;

public class InstanceContextListener extends GuiceServletContextListener {
	public InstanceContextListener() {
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new JpaPersistModule("instance"),
				new DispatchServerModule(), new BatchActionModule(),
				new ActionModule(), new DomainModule(), new HttpModule(),
				new InstanceServerModule(), new ParseModule());
	}
}
