/*
 * Source.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.domain;

import static com.google.common.base.Preconditions.*;

/*
 * TODO: using an enum means that adding new sources breaks interoperability
 */
public enum Source {
	NY_TIMES("www.nytimes.com", "New York Times"),

	WASHINGTON_POST("www.washingtonpost.com", "Washington Post"),

	CS_MONITOR("www.csmonitor.com", "Christian Science Monitor"),

	WS_JOURNAL("online.wsj.com", "Wall Street Journal"),

	REUTERS("www.reuters.com", "Reuters"),

	GUARDIAN("www.guardian.co.uk", "The Guardian"),

	ECONOMIST("www.economist.com", "The Economist");

	public static Source fromDomainName(String domainName) {
		checkNotNull(domainName);
		for (Source source : values()) {
			if (source.getDomainName().equals(domainName)) {
				return source;
			}
		}
		return null;
	}

	private final String domainName;

	private final String displayName;

	private Source(String domainName, String displayName) {
		this.domainName = domainName;
		this.displayName = displayName;
	}

	public String getDomainName() {
		return domainName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
