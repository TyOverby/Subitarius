/*
 * SearchUnavailableException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central.search;

public class SearchUnavailableException extends Exception {
	public SearchUnavailableException() {
		super();
	}

	public SearchUnavailableException(String message) {
		super(message);
	}

	public SearchUnavailableException(Throwable cause) {
		super(cause);
	}

	public SearchUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}
}
