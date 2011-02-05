/*
 * DeleteTag.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

public class DeleteTag implements AuthenticatedAction<MutationResult> {
	private String sessionId;

	private String tagName;

	// serialization support
	@SuppressWarnings("unused")
	private DeleteTag() {
	}

	public DeleteTag(String sessionId, String tagName) {
		checkNotNull(sessionId);
		checkNotNull(tagName);
		this.sessionId = sessionId;
		this.tagName = tagName;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	public String getTagName() {
		return tagName;
	}
}
