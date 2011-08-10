/*
 * GetUserResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import com.prealpha.dispatch.shared.Result;
import com.prealpha.extempdb.instance.shared.dto.UserDto;

public class GetUserResult implements Result {
	private UserDto user;

	// serialization support
	@SuppressWarnings("unused")
	private GetUserResult() {
	}

	public GetUserResult(UserDto user) {
		this.user = user;
	}

	public UserDto getUser() {
		return user;
	}
}
