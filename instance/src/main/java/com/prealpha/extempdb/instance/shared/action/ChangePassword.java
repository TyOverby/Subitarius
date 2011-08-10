/*
 * ChangePassword.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

public class ChangePassword implements AuthenticatedAction<MutationResult> {
	private String sessionId;

	private String currentPassword;

	private String newPassword;

	// serialization support
	@SuppressWarnings("unused")
	private ChangePassword() {
	}

	public ChangePassword(String sessionId, String currentPassword,
			String newPassword) {
		checkNotNull(sessionId);
		checkNotNull(currentPassword);
		checkNotNull(newPassword);
		this.sessionId = sessionId;
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}
}
