/*
 * RobotsExclusionException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.http;

import com.subitarius.util.http.robots.RobotsTxt;

public class RobotsExclusionException extends Exception {
	private static final long serialVersionUID = 1L;

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
