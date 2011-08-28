/*
 * Source.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.domain;

import static com.google.common.base.Preconditions.*;

/*
 * TODO: using an enum means that adding new sources breaks interoperability
 */
public enum Source {
	NY_TIMES("New York Times", "www.nytimes.com") {
	},

	WASHINGTON_POST("Washington Post", "www.washingtonpost.com") {
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

	CS_MONITOR("Christian Science Monitor", "www.csmonitor.com") {
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

	WS_JOURNAL("Wall Street Journal", "online.wsj.com") {
	},

	REUTERS("Reuters", "www.reuters.com") {
	},

	GUARDIAN("The Guardian", "www.guardian.co.uk") {
	},

	ECONOMIST("The Economist", "www.economist.com") {
	},

	AL_JAZEERA("Al Jazeera", "english.aljazeera.net") {
	},

	LA_TIMES("La Times", "www.latimes.com", "latimesblogs.latimes.com") {
	},

	BBC("BBC", "www.bbc.co.uk") {
	};

	public static Source fromUrl(String url) {
		int baseIndex;
		if (url.startsWith("http://")) {
			baseIndex = 7;
		} else if (url.startsWith("https://")) {
			baseIndex = 8;
		} else {
			throw new IllegalArgumentException();
		}

		int index = url.indexOf('/', baseIndex);
		String domainName = url.substring(baseIndex, index);
		return fromDomainName(domainName);
	}

	public static Source fromDomainName(String domainName) {
		checkNotNull(domainName);
		for (Source source : values()) {
			if (source.isSource(domainName)) {
				return source;
			}
		}
		return null;
	}

	private final String[] domainNames;
	private final String displayName;

	private Source(String displayName, String... domainNames) {
		this.displayName = displayName;
		this.domainNames = domainNames;
	}

	// I basically made this to be overriden
	public boolean isSource(String domainName) {
		for (String s : getDomainNames()) {
			if (s.equals(domainName)) {
				return true;
			}
		}
		return false;
	}

	String canonicalize(String rawUrl) {
		int parameterIndex = rawUrl.indexOf('?');
		if (parameterIndex >= 0) {
			return rawUrl.substring(0, parameterIndex);
		} else {
			return rawUrl;
		}
	}

	public String[] getDomainNames() {
		return domainNames;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
