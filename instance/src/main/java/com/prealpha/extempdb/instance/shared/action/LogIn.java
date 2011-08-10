/*
 * LogIn.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

public class LogIn implements AuthenticatedAction<GetUserResult> {
	private String sessionId;

	private String name;

	private String password;

	// serialization support
	@SuppressWarnings("unused")
	private LogIn() {
	}

	public LogIn(String sessionId, String name, String password) {
		checkNotNull(sessionId);
		checkNotNull(name);
		checkNotNull(password);
		this.sessionId = sessionId;
		this.name = name;
		this.password = password;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}
}
