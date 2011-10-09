/*
 * TestLauncher.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;

final class TestLauncher {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new LauncherModule(),
				new TestingLauncherModule());
		Launcher launcher = injector.getInstance(Launcher.class);
		launcher.launch();
	}
}
