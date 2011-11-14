/*
 * GetArticleResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.action;

import com.prealpha.xylophone.shared.Result;
import com.subitarius.action.dto.ArticleDto;

public final class GetArticleResult implements Result {
	private ArticleDto article;

	// serialization support
	@SuppressWarnings("unused")
	private GetArticleResult() {
	}

	public GetArticleResult(ArticleDto article) {
		this.article = article;
	}

	public ArticleDto getArticle() {
		return article;
	}
}
