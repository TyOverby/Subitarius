/*
 * AddArticle.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

public class AddArticle implements AuthenticatedAction<AddArticleResult> {
	private String sessionId;

	private String url;

	// serialization support
	@SuppressWarnings("unused")
	private AddArticle() {
	}

	public AddArticle(String sessionId, String url) {
		checkNotNull(sessionId);
		checkNotNull(url);
		this.sessionId = sessionId;
		this.url = url;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	public String getUrl() {
		return url;
	}
}
