/*
 * TestingInstanceServer.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.gwt.dev.DevMode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.instance.launcher.TestingLauncherModule.ServerUrl;

final class TestingInstanceServer implements InstanceServer {
	private final Provider<DevMode> devModeProvider;

	private final URL serverUrl;

	private DevMode devMode;

	@Inject
	private TestingInstanceServer(Provider<DevMode> devModeProvider,
			@ServerUrl String serverUrl) throws MalformedURLException {
		this.devModeProvider = devModeProvider;
		this.serverUrl = new URL(serverUrl);
	}

	@Override
	public synchronized URL start() {
		devMode = devModeProvider.get();
		devMode.run();
		return serverUrl;
	}

	@Override
	public synchronized void stop() {
		devMode.onDone();
		devMode = null;
	}
}
