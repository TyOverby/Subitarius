/*
 * GetParagraphs.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.filter.CacheableAction;
import com.prealpha.dispatch.shared.filter.MergeableAction;

public final class GetParagraphs implements
		CacheableAction<GetParagraphsResult>,
		MergeableAction<GetParagraphsResult> {
	private String articleHash;

	// serialization support
	@SuppressWarnings("unused")
	private GetParagraphs() {
	}

	public GetParagraphs(String articleHash) {
		checkNotNull(articleHash);
		this.articleHash = articleHash;
	}

	public String getArticleHash() {
		return articleHash;
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
				+ ((articleHash == null) ? 0 : articleHash.hashCode());
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
		if (articleHash == null) {
			if (other.articleHash != null) {
				return false;
			}
		} else if (!articleHash.equals(other.articleHash)) {
			return false;
		}
		return true;
	}
}
