/*
 * UserSessionToken.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.id;

import static com.google.common.base.Preconditions.*;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserSessionToken implements IsSerializable {
	private String token;

	// serialization support
	@SuppressWarnings("unused")
	private UserSessionToken() {
	}

	public UserSessionToken(String token) {
		setToken(token);
	}

	public String getToken() {
		return token;
	}

	private void setToken(String token) {
		checkNotNull(token);
		this.token = token;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		if (!(obj instanceof UserSessionToken)) {
			return false;
		}
		UserSessionToken other = (UserSessionToken) obj;
		if (token == null) {
			if (other.token != null) {
				return false;
			}
		} else if (!token.equals(other.token)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return token;
	}
}
