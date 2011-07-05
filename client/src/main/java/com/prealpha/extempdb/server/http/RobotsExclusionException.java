/*
 * RobotsExclusionException.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.http;

import com.prealpha.extempdb.server.http.robots.RobotsTxt;

public class RobotsExclusionException extends Exception {
	private final RobotsTxt robotsTxt;

	public RobotsExclusionException() {
		super();
		robotsTxt = null;
	}

	public RobotsExclusionException(String url) {
		this(url, null);
	}

	public RobotsExclusionException(RobotsTxt robotsTxt) {
		super();
		this.robotsTxt = robotsTxt;
	}

	public RobotsExclusionException(String url, RobotsTxt robotsTxt) {
		super("request blocked by robots exclusion check: " + url);
		this.robotsTxt = robotsTxt;
	}

	public RobotsTxt getRobotsTxt() {
		return robotsTxt;
	}
}
