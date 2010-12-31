/*
 * ArticleParseException.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

public class ArticleParseException extends Exception {
	public ArticleParseException() {
		super();
	}

	public ArticleParseException(String message) {
		super(message);
	}

	public ArticleParseException(Throwable cause) {
		super(cause);
	}

	public ArticleParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
