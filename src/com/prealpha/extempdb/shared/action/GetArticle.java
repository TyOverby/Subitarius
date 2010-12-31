/*
 * GetArticle.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.Date;

import com.prealpha.extempdb.shared.id.ArticleId;
import com.prealpha.gwt.dispatch.client.filter.CacheableAction;
import com.prealpha.gwt.dispatch.client.filter.MergeableAction;

public class GetArticle implements CacheableAction<GetArticleResult>,
		MergeableAction<GetArticleResult> {
	private static final long EXPIRY_INTERVAL = 1000 * 60 * 60 * 24; // 24 hours

	private ArticleId id;

	private long cacheExpiry;

	// serialization support
	@SuppressWarnings("unused")
	private GetArticle() {
	}

	public GetArticle(ArticleId id) {
		checkNotNull(id);
		this.id = id;

		// we want the next midnight UTC after the current time
		cacheExpiry = (System.currentTimeMillis() / EXPIRY_INTERVAL)
				* EXPIRY_INTERVAL + EXPIRY_INTERVAL;
	}

	public ArticleId getId() {
		return id;
	}

	@Override
	public Date getCacheExpiry(GetArticleResult result) {
		return new Date(cacheExpiry);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
