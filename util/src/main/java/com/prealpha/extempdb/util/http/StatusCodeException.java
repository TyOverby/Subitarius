/*
 * StatusCodeException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.util.http;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;

public class StatusCodeException extends IOException {
	private final int statusCode;

	public StatusCodeException(int statusCode) {
		super("received status code: " + statusCode);
		checkArgument(statusCode >= 0 && statusCode < 1000);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
