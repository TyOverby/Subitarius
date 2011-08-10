/*
 * GetParagraphs.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.filter.CacheableAction;
import com.prealpha.dispatch.shared.filter.MergeableAction;

public class GetParagraphs implements CacheableAction<GetParagraphsResult>,
		MergeableAction<GetParagraphsResult> {
	private Long articleId;

	// serialization support
	@SuppressWarnings("unused")
	private GetParagraphs() {
	}

	public GetParagraphs(Long articleId) {
		checkNotNull(articleId);
		this.articleId = articleId;
	}

	public Long getArticleId() {
		return articleId;
	}

	@Override
	public long getCacheExpiry(GetParagraphsResult result) {
		// cache indefinitely, paragraphs will never change
		return Long.MAX_VALUE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((articleId == null) ? 0 : articleId.hashCode());
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
		if (!(obj instanceof GetParagraphs)) {
			return false;
		}
		GetParagraphs other = (GetParagraphs) obj;
		if (articleId == null) {
			if (other.articleId != null) {
				return false;
			}
		} else if (!articleId.equals(other.articleId)) {
			return false;
		}
		return true;
	}
}
