/*
 * Slf4jBridgeContextListener.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.logging;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.bridge.SLF4JBridgeHandler;

public final class Slf4jBridgeContextListener implements ServletContextListener {
	public Slf4jBridgeContextListener() {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// remove anything already attached to JUL
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		SLF4JBridgeHandler.install();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		SLF4JBridgeHandler.uninstall();
	}
}
