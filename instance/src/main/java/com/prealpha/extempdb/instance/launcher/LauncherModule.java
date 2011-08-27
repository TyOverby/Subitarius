/*
 * LauncherModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.awt.SplashScreen;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import com.prealpha.extempdb.instance.launcher.Launcher.ServerUri;
import com.prealpha.extempdb.instance.server.InstanceContextListener;

public final class LauncherModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(InstanceServer.class).to(InstanceServerImpl.class).in(
				Singleton.class);
		bindConstant().annotatedWith(ServerUri.class).to(
				"http://127.0.0.1:8080/");
	}

	@Provides
	SplashScreen getSplashScreen() {
		return SplashScreen.getSplashScreen();
	}

	@Provides
	@Inject
	Server getServer(ContextHandler contextHandler,
			InstanceContextListener contextListener,
			ServletHandler servletHandler, GuiceFilter filter) {
		Server server = new Server(8080);
		contextHandler.addHandler(servletHandler);
		contextHandler.addEventListener(contextListener);
		FilterHolder filterHolder = servletHandler.newFilterHolder();
		filterHolder.setFilter(filter);
		servletHandler.addFilter(filterHolder);
		server.setHandler(contextHandler);
		return server;
	}
}
