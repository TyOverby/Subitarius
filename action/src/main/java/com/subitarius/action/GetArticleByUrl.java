/*
 * GetArticleByUrl.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.action;

import static com.google.common.base.Preconditions.*;

import com.subitarius.action.dto.ArticleUrlDto;

public final class GetArticleByUrl extends GetArticle {
	private String articleUrl;

	// serialization support
	@SuppressWarnings("unused")
	private GetArticleByUrl() {
	}

	public GetArticleByUrl(String articleUrl) {
		checkNotNull(articleUrl);
		this.articleUrl = articleUrl;
	}

	public GetArticleByUrl(ArticleUrlDto dto) {
		this(dto.getUrl());
	}

	public String getArticleUrl() {
		return articleUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((articleUrl == null) ? 0 : articleUrl.hashCode());
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
		if (!(obj instanceof GetArticleByUrl)) {
			return false;
		}
		GetArticleByUrl other = (GetArticleByUrl) obj;
		if (articleUrl == null) {
			if (other.articleUrl != null) {
				return false;
			}
		} else if (!articleUrl.equals(other.articleUrl)) {
			return false;
		}
		return true;
	}
}
