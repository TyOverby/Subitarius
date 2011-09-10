/*
 * GetArticle.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.action;

import static com.google.common.base.Preconditions.*;

public final class GetArticleByHash extends GetArticle {
	private String articleHash;

	// serialization support
	@SuppressWarnings("unused")
	private GetArticleByHash() {
	}

	public GetArticleByHash(String articleHash) {
		checkNotNull(articleHash);
		this.articleHash = articleHash;
	}

	public String getArticleHash() {
		return articleHash;
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
		if (!(obj instanceof GetArticleByHash)) {
			return false;
		}
		GetArticleByHash other = (GetArticleByHash) obj;
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
