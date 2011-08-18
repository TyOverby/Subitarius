/*
 * InstanceServer.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.net.URL;

public interface InstanceServer {
	URL start();
	
	void stop();
}
