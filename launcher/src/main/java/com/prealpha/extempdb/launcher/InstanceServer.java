/*
 * InstanceServer.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.launcher;

interface InstanceServer {
	void start() throws InstanceServerException;

	void stop() throws InstanceServerException;
}
