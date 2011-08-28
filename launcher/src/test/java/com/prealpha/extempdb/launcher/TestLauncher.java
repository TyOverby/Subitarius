/*
 * TestLauncher.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

final class TestLauncher {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(Modules.override(
				new LauncherModule()).with(new TestingLauncherModule()));
		Launcher launcher = injector.getInstance(Launcher.class);
		launcher.launch();
	}
}
