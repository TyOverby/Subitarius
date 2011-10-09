/*
 * LauncherModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.launcher;

import java.awt.Desktop;
import java.awt.SplashScreen;
import java.util.ResourceBundle;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public final class LauncherModule extends AbstractModule {
	public LauncherModule() {
	}

	@Override
	protected void configure() {
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
}
