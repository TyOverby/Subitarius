/*
 * LogOut.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

public class LogOut implements AuthenticatedAction<MutationResult> {
	private String sessionId;

	// serialization support
	@SuppressWarnings("unused")
	private LogOut() {
	}

	public LogOut(String sessionId) {
		checkNotNull(sessionId);
		this.sessionId = sessionId;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}
}
