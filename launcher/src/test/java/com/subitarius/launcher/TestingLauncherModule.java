/*
 * TestingLauncherModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.launcher;

import java.io.File;

import com.google.gwt.core.ext.ServletContainerLauncher;
import com.google.gwt.dev.DevMode;
import com.google.gwt.dev.shell.jetty.JettyLauncher;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.subitarius.launcher.Launcher.ServerUri;

public final class TestingLauncherModule extends AbstractModule {
	public TestingLauncherModule() {
	}

	@Override
	protected void configure() {
		bind(InstanceServer.class).to(TestingInstanceServer.class).in(
				Singleton.class);
		bind(DevMode.class).to(TestingDevMode.class);
		bind(ServletContainerLauncher.class).to(JettyLauncher.class);
		bindConstant().annotatedWith(ServerUri.class).to(
				"http://127.0.0.1:8888/?gwt.codesvr=127.0.0.1:9997");
	}

	private static final class TestingDevMode extends DevMode {
		@Inject
		private TestingDevMode(ServletContainerLauncher scl) {
			options.setServletContainerLauncher(scl);
			options.setConnectAddress("127.0.0.1");
			options.setBindAddress("127.0.0.1");
			options.setWarDir(new File("../instance/target/webapp"));
			options.setPort(8888);
			options.setCodeServerPort(9997);
			options.addStartupURL("/");
			options.addModuleName("com.subitarius.instance.Subitarius");
		}
	}
}
