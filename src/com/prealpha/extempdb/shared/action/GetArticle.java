/*
 * GetArticle.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.filter.CacheableAction;
import com.prealpha.dispatch.shared.filter.MergeableAction;

public class GetArticle implements CacheableAction<GetArticleResult>,
		MergeableAction<GetArticleResult> {
	private static final long EXPIRY_INTERVAL = 1000 * 60 * 60 * 24; // 24 hours

	private Long articleId;

	private long cacheExpiry;

	// serialization support
	@SuppressWarnings("unused")
	private GetArticle() {
	}

	public GetArticle(Long articleId) {
		checkNotNull(articleId);
		this.articleId = articleId;

		// we want the next midnight UTC after the current time
		cacheExpiry = (System.currentTimeMillis() / EXPIRY_INTERVAL)
				* EXPIRY_INTERVAL + EXPIRY_INTERVAL;
	}

	public Long getArticleId() {
		return articleId;
	}

	@Override
	public long getCacheExpiry(GetArticleResult result) {
		return cacheExpiry;
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
		if (!(obj instanceof GetArticle)) {
			return false;
		}
		GetArticle other = (GetArticle) obj;
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
