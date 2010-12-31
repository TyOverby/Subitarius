/*
 * GetMappingsByArticle.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.extempdb.shared.dto.ArticleDto;
import com.prealpha.extempdb.shared.dto.TagMappingDto;

public class GetMappingsByArticle extends GetMappings {
	private ArticleDto article;

	// serialization support
	@SuppressWarnings("unused")
	private GetMappingsByArticle() {
	}

	public GetMappingsByArticle(ArticleDto article) {
		checkNotNull(article);
		this.article = article;
	}

	public ArticleDto getArticle() {
		return article;
	}

	@Override
	public boolean apply(TagMappingDto mapping) {
		return (article.equals(mapping.getArticle()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((article == null) ? 0 : article.hashCode());
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
		if (article == null) {
			if (other.article != null) {
				return false;
			}
		} else if (!article.equals(other.article)) {
			return false;
		}
		return true;
	}
}
