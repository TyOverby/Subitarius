/*
 * LauncherModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.launcher;

import java.awt.Desktop;
import java.awt.SplashScreen;
import java.util.ResourceBundle;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.subitarius.launcher.Launcher.ServerUri;

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
	Desktop getDesktop() {
		return Desktop.getDesktop();
	}
	
	@Provides
	ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle(getClass().getCanonicalName());
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
