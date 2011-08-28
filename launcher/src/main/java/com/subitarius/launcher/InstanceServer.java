/*
 * InstanceServer.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.launcher;

interface InstanceServer {
	void start() throws InstanceServerException;

	void stop() throws InstanceServerException;
}
