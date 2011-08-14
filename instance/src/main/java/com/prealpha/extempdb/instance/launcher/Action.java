/*
 * Action.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.net.URL;
import java.util.Date;

public interface Action {
	Date getTimestamp();
	
	URL getUrl();
	
	String toString();
}
