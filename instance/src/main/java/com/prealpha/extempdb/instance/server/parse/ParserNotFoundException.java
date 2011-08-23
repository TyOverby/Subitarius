/*
 * ParserNotFoundException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import static com.google.common.base.Preconditions.checkNotNull;

import com.prealpha.extempdb.domain.Source;

public class ParserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String url;

	private final Source source;

	public ParserNotFoundException(String url) {
		super("parser not found for URL: " + url);
		checkNotNull(url);
		this.url = url;
		source = null;
	}

	public ParserNotFoundException(Source source) {
		super("parser not found for source: " + source);
		checkNotNull(source);
		url = null;
		this.source = source;
	}

	public String getUrl() {
		return url;
	}

	public Source getSource() {
		return source;
	}
}
