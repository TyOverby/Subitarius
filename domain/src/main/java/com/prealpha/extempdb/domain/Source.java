/*
 * Source.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

/*
 * TODO: using an enum means that adding new sources breaks interoperability
 */
public enum Source {
	NY_TIMES("www.nytimes.com", "New York Times") {
	},

	WASHINGTON_POST("www.washingtonpost.com", "Washington Post") {
		@Override
		String canonicalize(String rawUrl) {
			rawUrl = super.canonicalize(rawUrl);
			if (rawUrl.matches(".*_story_\\d+.html")) {
				// page numbers
				int index = rawUrl.lastIndexOf("_story");
				return rawUrl.substring(0, index) + "_story.html";
			} else if (rawUrl.endsWith("_singlePage.html")) {
				// single page version
				int index = rawUrl.lastIndexOf("_singlePage");
				return rawUrl.substring(0, index) + "_story.html";
			} else if (rawUrl.endsWith("_print.html")) {
				// printable version
				int index = rawUrl.lastIndexOf("_print");
				return rawUrl.substring(0, index) + "_story.html";
			} else {
				return rawUrl;
			}
		}
	},

	CS_MONITOR("www.csmonitor.com", "Christian Science Monitor") {
		@Override
		String canonicalize(String rawUrl) {
			rawUrl = super.canonicalize(rawUrl);
			if (rawUrl.matches(".*/\\(page\\)/\\d+")) {
				int index = rawUrl.lastIndexOf("/(page)");
				return rawUrl.substring(0, index);
			} else {
				return rawUrl;
			}
		}
	},

	WS_JOURNAL("online.wsj.com", "Wall Street Journal") {
	},

	REUTERS("www.reuters.com", "Reuters") {
	},

	GUARDIAN("www.guardian.co.uk", "The Guardian") {
	},

	ECONOMIST("www.economist.com", "The Economist") {
	};

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

	String canonicalize(String rawUrl) {
		int parameterIndex = rawUrl.indexOf('?');
		if (parameterIndex >= 0) {
			return rawUrl.substring(0, parameterIndex);
		} else {
			return rawUrl;
		}
	}

	public String getDomainName() {
		return domainName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
