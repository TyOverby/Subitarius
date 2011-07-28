/*
 * TestLoggingModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.util.logging;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.prealpha.extempdb.util.logging.Slf4jTypeListener;

public final class TestLoggingModule extends AbstractModule {
	public TestLoggingModule() {
	}

	@Override
	protected void configure() {
		SLF4JBridgeHandler.install();
		bindListener(Matchers.any(), new Slf4jTypeListener());
	}
}
