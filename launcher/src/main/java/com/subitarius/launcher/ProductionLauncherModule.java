/*
 * ProductionLauncherModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.launcher;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.subitarius.launcher.Launcher.ServerUri;

public final class ProductionLauncherModule extends AbstractModule {
	public ProductionLauncherModule() {
	}

	@Override
	protected void configure() {
		bind(InstanceServer.class).to(InstanceServerImpl.class).in(
				Singleton.class);
		bindConstant().annotatedWith(ServerUri.class).to(
				"http://127.0.0.1:8080/");
	}

	@Provides
	Server getServer() {
		Server server = new Server(8080);
		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setWar("./war");
		server.setHandler(context);
		return server;
	}
}
