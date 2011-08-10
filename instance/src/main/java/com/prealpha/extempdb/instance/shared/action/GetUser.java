/*
 * GetUser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.filter.MergeableAction;

public class GetUser implements AuthenticatedAction<GetUserResult>,
		MergeableAction<GetUserResult> {
	private String sessionId;

	// serialization support
	@SuppressWarnings("unused")
	private GetUser() {
	}

	public GetUser(String sessionId) {
		checkNotNull(sessionId);
		this.sessionId = sessionId;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sessionId == null) ? 0 : sessionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GetUser)) {
			return false;
		}
		GetUser other = (GetUser) obj;
		if (sessionId == null) {
			if (other.sessionId != null) {
				return false;
			}
		} else if (!sessionId.equals(other.sessionId)) {
			return false;
		}
		return true;
	}
}
