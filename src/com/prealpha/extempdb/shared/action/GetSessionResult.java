/*
 * GetSessionResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import com.prealpha.extempdb.shared.dto.UserSessionDto;
import com.prealpha.gwt.dispatch.shared.Result;

public class GetSessionResult implements Result {
	private UserSessionDto session;

	// serialization support
	@SuppressWarnings("unused")
	private GetSessionResult() {
	}

	public GetSessionResult(UserSessionDto session) {
		this.session = session;
	}

	public UserSessionDto getSession() {
		return session;
	}
}
