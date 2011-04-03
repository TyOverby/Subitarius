/*
 * Slf4jBridgeFilter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.bridge.SLF4JBridgeHandler;

final class Slf4jBridgeFilter implements Filter {
	public Slf4jBridgeFilter() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		SLF4JBridgeHandler.install();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		SLF4JBridgeHandler.uninstall();
	}
}
