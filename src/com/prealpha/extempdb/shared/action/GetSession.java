/*
 * GetSession.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.Date;

import com.prealpha.extempdb.shared.dto.UserSessionDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;
import com.prealpha.gwt.dispatch.client.filter.CacheableAction;
import com.prealpha.gwt.dispatch.client.filter.MergeableAction;

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
	public Date getCacheExpiry(GetSessionResult result) {
		UserSessionDto session = result.getSession();
		return session.getExpiry();
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
