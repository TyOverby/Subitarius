/*
 * AddMapping.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

public class AddMapping implements AuthenticatedAction<MutationResult> {
	private String sessionId;

	private String tagName;

	private Long articleId;

	// serialization support
	@SuppressWarnings("unused")
	private AddMapping() {
	}

	public AddMapping(String sessionId, String tagName, Long articleId) {
		checkNotNull(sessionId);
		checkNotNull(tagName);
		checkNotNull(articleId);

		this.sessionId = sessionId;
		this.tagName = tagName;
		this.articleId = articleId;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	public String getTagName() {
		return tagName;
	}

	public Long getArticleId() {
		return articleId;
	}
}