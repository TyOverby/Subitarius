/*
 * LoggingModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public final class LoggingModule extends AbstractModule {
	public LoggingModule() {
	}

	@Override
	protected void configure() {
		SLF4JBridgeHandler.install();
		bindListener(Matchers.any(), new Slf4jTypeListener());
	}
}
