/*
 * Launcher.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

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

	public static void main(String[] args) throws InstanceServerException,
			IOException {
		Injector injector = Guice.createInjector(new LauncherModule());
		Launcher launcher = injector.getInstance(Launcher.class);
		launcher.launch();
	}

	private final InstanceServer instanceServer;

	private final URI serverUri;

	@Inject
	private Launcher(InstanceServer instanceServer, @ServerUri String serverUri)
			throws MalformedURLException, URISyntaxException {
		this.instanceServer = instanceServer;
		this.serverUri = new URI(serverUri);
	}

	/*
	 * TODO: this really shouldn't throw anything
	 */
	void launch() throws InstanceServerException, IOException {
		instanceServer.start();
		Desktop.getDesktop().browse(serverUri);
	}
}
