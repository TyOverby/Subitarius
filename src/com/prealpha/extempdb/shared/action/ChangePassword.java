/*
 * ChangePassword.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.Action;
import com.prealpha.extempdb.shared.id.UserSessionToken;

public class ChangePassword implements Action<MutationResult> {
	private UserSessionToken sessionToken;

	private String currentPassword;

	private String newPassword;

	// serialization support
	@SuppressWarnings("unused")
	private ChangePassword() {
	}

	public ChangePassword(UserSessionToken sessionToken,
			String currentPassword, String newPassword) {
		checkNotNull(sessionToken);
		checkNotNull(currentPassword);
		checkNotNull(newPassword);
		this.sessionToken = sessionToken;
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
	}

	public UserSessionToken getSessionToken() {
		return sessionToken;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}
}
