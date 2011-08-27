/*
 * TestingInstanceServer.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import com.google.gwt.dev.DevMode;
import com.google.inject.Inject;
import com.google.inject.Provider;

final class TestingInstanceServer implements InstanceServer {
	private final Provider<DevMode> devModeProvider;

	private DevMode devMode;

	@Inject
	private TestingInstanceServer(Provider<DevMode> devModeProvider) {
		this.devModeProvider = devModeProvider;
	}

	@Override
	public synchronized void start() throws InstanceServerException {
		devMode = devModeProvider.get();
		devMode.run();
	}

	@Override
	public synchronized void stop() {
		devMode.onDone();
		devMode = null;
	}
}
