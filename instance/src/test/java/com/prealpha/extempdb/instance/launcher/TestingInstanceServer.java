/*
 * TestingInstanceServer.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gwt.dev.DevMode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.instance.launcher.TestingLauncherModule.ServerUrl;

final class TestingInstanceServer implements InstanceServer {
	private final Provider<DevMode> devModeProvider;

	private final Timer timer;

	private final URL serverUrl;

	private DevMode devMode;

	@Inject
	private TestingInstanceServer(Provider<DevMode> devModeProvider,
			Timer timer, @ServerUrl String serverUrl)
			throws MalformedURLException {
		this.devModeProvider = devModeProvider;
		this.timer = timer;
		this.serverUrl = new URL(serverUrl);
	}

	@Override
	public URL start() {
		synchronized (this) {
			devMode = devModeProvider.get();
		}

		// start() is going to be called on the Swing thread
		// however, DevMode uses EventQueue.invokeAndWait internally
		// so we need to run() on some other thread for it to not complain
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				synchronized (TestingInstanceServer.this) {
					devMode.run();
				}
			}
		}, 0);

		return serverUrl;
	}

	@Override
	public synchronized void stop() {
		devMode.onDone();
		devMode = null;
	}
}
