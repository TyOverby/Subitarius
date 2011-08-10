/*
 * InvalidSessionException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.ActionException;

public class InvalidSessionException extends ActionException {
	private String cookieId;

	private String payloadId;

	// serialization support
	@SuppressWarnings("unused")
	private InvalidSessionException() {
	}

	public InvalidSessionException(String cookieId, String payloadId) {
		super("invalid session ID in payload");
		initCause(null);

		checkNotNull(cookieId);
		checkArgument(!cookieId.equals(payloadId));

		this.cookieId = cookieId;
		this.payloadId = payloadId;
	}

	public String getCookieId() {
		return cookieId;
	}

	public String getPayloadId() {
		return payloadId;
	}
}
