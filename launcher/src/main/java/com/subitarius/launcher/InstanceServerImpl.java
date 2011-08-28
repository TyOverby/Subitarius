/*
 * InstanceServerImpl.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.launcher;

import org.mortbay.jetty.Server;

import com.google.inject.Inject;

final class InstanceServerImpl implements InstanceServer {
	private final Server server;

	@Inject
	private InstanceServerImpl(Server server) {
		this.server = server;
	}

	@Override
	public void start() throws InstanceServerException {
		try {
			server.start();
		} catch (Error e) {
			throw e;
		} catch (RuntimeException rx) {
			throw rx;
		} catch (Exception x) {
			throw new InstanceServerException(x);
		}
	}

	@Override
	public void stop() throws InstanceServerException {
		try {
			server.stop();
			server.destroy();
		} catch (Error e) {
			throw e;
		} catch (RuntimeException rx) {
			throw rx;
		} catch (Exception x) {
			throw new InstanceServerException(x);
		}
	}
}
