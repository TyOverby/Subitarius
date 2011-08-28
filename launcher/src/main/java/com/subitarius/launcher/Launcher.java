/*
 * Launcher.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.launcher;

import java.awt.Desktop;
import java.awt.SplashScreen;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

final class Launcher {
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	static @interface ServerUri {
	}

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		System.setProperty("user.timezone", "UTC");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		Injector injector = Guice.createInjector(new LauncherModule());
		Launcher launcher = injector.getInstance(Launcher.class);
		launcher.launch();
	}

	private final InstanceServer instanceServer;

	private final SplashScreen splashScreen;

	private final Desktop desktop;

	private final URI serverUri;

	private final LauncherUi ui;

	@Inject
	private Launcher(InstanceServer instanceServer, SplashScreen splashScreen,
			Desktop desktop, @ServerUri String serverUri, LauncherUi ui)
			throws URISyntaxException {
		this.instanceServer = instanceServer;
		this.splashScreen = splashScreen;
		this.desktop = desktop;
		this.serverUri = new URI(serverUri);
		this.ui = ui;
	}

	void launch() {
		try {
			instanceServer.start(); // blocks in dev mode
			if (splashScreen.isVisible()) { // false in dev mode
				splashScreen.close();
				desktop.browse(serverUri);
				ui.show();
			}
		} catch (InstanceServerException isx) {
			ui.handleException(isx);
		} catch (IOException iox) {
			ui.handleException(iox);
		}
	}
}
