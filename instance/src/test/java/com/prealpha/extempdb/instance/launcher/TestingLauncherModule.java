/*
 * TestingLauncherModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gwt.core.ext.ServletContainerLauncher;
import com.google.gwt.dev.DevMode;
import com.google.gwt.dev.shell.jetty.JettyLauncher;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Singleton;

public final class TestingLauncherModule extends AbstractModule {
	public TestingLauncherModule() {
	}

	@Override
	protected void configure() {
		bind(InstanceServer.class).to(TestingInstanceServer.class).in(
				Singleton.class);
		bind(DevMode.class).to(TestingDevMode.class);
		bind(ServletContainerLauncher.class).to(JettyLauncher.class);
		bindConstant().annotatedWith(ServerUrl.class).to(
				"http://127.0.0.1:8888/?gwt.codesvr=127.0.0.1:9997");
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public static @interface ServerUrl {
	}

	private static final class TestingDevMode extends DevMode {
		@Inject
		private TestingDevMode(ServletContainerLauncher scl) {
			options.setServletContainerLauncher(scl);
			options.setConnectAddress("127.0.0.1");
			options.setBindAddress("127.0.0.1");
			options.setWarDir(new File("./target/webapp"));
			options.setPort(8888);
			options.setCodeServerPort(9997);
			options.addStartupURL("/");
			options.addModuleName("com.prealpha.extempdb.instance.ExtempDb");
		}
	}
}
