/*
 * TestLauncher.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;

final class TestLauncher {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new JpaPersistModule(
				"instance-test"), new LauncherModule());
		Launcher launcher = injector.getInstance(Launcher.class);
		launcher.launch();
	}
}
