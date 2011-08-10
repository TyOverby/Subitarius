/*
 * GetMappingsByArticle.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;

public class GetMappingsByArticle extends GetMappings {
	private Long articleId;

	// serialization support
	@SuppressWarnings("unused")
	private GetMappingsByArticle() {
	}

	public GetMappingsByArticle(Long articleId) {
		checkNotNull(articleId);
		this.articleId = articleId;
	}

	public Long getArticleId() {
		return articleId;
	}

	@Override
	public boolean apply(TagMappingDto mapping) {
		return (articleId.equals(mapping.getKey().getArticleId()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((articleId == null) ? 0 : articleId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof GetMappingsByArticle)) {
			return false;
		}
		GetMappingsByArticle other = (GetMappingsByArticle) obj;
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
