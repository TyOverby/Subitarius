/*
 * ArticleParseException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import static com.google.common.base.Preconditions.*;

public class ArticleParseException extends Exception {
	private final String url;

	public ArticleParseException(String url) {
		this(url, null);
	}

	public ArticleParseException(String url, Throwable cause) {
		super("raised an exception while parsing article, URL " + url, cause);
		checkNotNull(url);
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
