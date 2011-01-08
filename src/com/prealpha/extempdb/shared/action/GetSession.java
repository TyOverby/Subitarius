/*
 * GetSession.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.CacheableAction;
import com.prealpha.dispatch.shared.MergeableAction;
import com.prealpha.extempdb.shared.dto.UserSessionDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;

public class GetSession implements CacheableAction<GetSessionResult>,
		MergeableAction<GetSessionResult> {
	private UserSessionToken sessionToken;

	// serialization support
	@SuppressWarnings("unused")
	private GetSession() {
	}

	public GetSession(UserSessionToken sessionToken) {
		checkNotNull(sessionToken);
		this.sessionToken = sessionToken;
	}

	public UserSessionToken getSessionToken() {
		return sessionToken;
	}

	@Override
	public long getCacheExpiry(GetSessionResult result) {
		UserSessionDto session = result.getSession();
		return session.getExpiry().getTime();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sessionToken == null) ? 0 : sessionToken.hashCode());
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
		if (!(obj instanceof GetSession)) {
			return false;
		}
		GetSession other = (GetSession) obj;
		if (sessionToken == null) {
			if (other.sessionToken != null) {
				return false;
			}
		} else if (!sessionToken.equals(other.sessionToken)) {
			return false;
		}
		return true;
	}
}
