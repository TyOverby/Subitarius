/*
 * GetMappingsByArticle.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.subitarius.instance.shared.dto.TagMappingDto;

public final class GetMappingsByArticle extends GetMappings {
	private String articleUrlHash;

	// serialization support
	@SuppressWarnings("unused")
	private GetMappingsByArticle() {
	}

	public GetMappingsByArticle(String articleUrlHash) {
		checkNotNull(articleUrlHash);
		this.articleUrlHash = articleUrlHash;
	}

	public String getArticleUrlHash() {
		return articleUrlHash;
	}

	@Override
	public boolean apply(TagMappingDto mapping) {
		return (articleUrlHash.equals(mapping.getArticleUrl().getHash()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((articleUrlHash == null) ? 0 : articleUrlHash.hashCode());
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
		if (!(obj instanceof GetMappingsByArticle)) {
			return false;
		}
		GetMappingsByArticle other = (GetMappingsByArticle) obj;
		if (articleUrlHash == null) {
			if (other.articleUrlHash != null) {
				return false;
			}
		} else if (!articleUrlHash.equals(other.articleUrlHash)) {
			return false;
		}
		return true;
	}
}
