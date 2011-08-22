/*
 * GetArticleResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import com.prealpha.dispatch.shared.Result;
import com.prealpha.extempdb.instance.shared.dto.ArticleDto;

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
