/*
 * AddMapping.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.Action;

public class AddMapping implements Action<MutationResult> {
	private String tagName;

	private Long articleId;

	// serialization support
	@SuppressWarnings("unused")
	private AddMapping() {
	}

	public AddMapping(String tagName, Long articleId) {
		checkNotNull(tagName);
		checkNotNull(articleId);
		this.tagName = tagName;
		this.articleId = articleId;
	}

	public String getTagName() {
		return tagName;
	}

	public Long getArticleId() {
		return articleId;
	}
}