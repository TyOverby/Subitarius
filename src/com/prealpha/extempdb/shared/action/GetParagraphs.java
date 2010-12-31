/*
 * GetParagraphs.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.Date;

import com.prealpha.extempdb.shared.dto.ArticleDto;
import com.prealpha.gwt.dispatch.client.filter.CacheableAction;
import com.prealpha.gwt.dispatch.client.filter.MergeableAction;

public class GetParagraphs implements CacheableAction<GetParagraphsResult>,
		MergeableAction<GetParagraphsResult> {
	private ArticleDto article;

	// serialization support
	@SuppressWarnings("unused")
	private GetParagraphs() {
	}

	public GetParagraphs(ArticleDto article) {
		checkNotNull(article);
		this.article = article;
	}

	public ArticleDto getArticle() {
		return article;
	}

	@Override
	public Date getCacheExpiry(GetParagraphsResult result) {
		// cache indefinitely, paragraphs will never change
		return new Date(Long.MAX_VALUE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((article == null) ? 0 : article.hashCode());
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
