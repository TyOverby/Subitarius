/*
 * LogIn.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.gwt.dispatch.shared.Action;

public class LogIn implements Action<GetSessionResult> {
	private String name;

	private String password;

	// serialization support
	@SuppressWarnings("unused")
	private LogIn() {
	}

	public LogIn(String name, String password) {
		checkNotNull(name);
		checkNotNull(password);
		this.name = name;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}
}
