/*
 * GetArticleResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import com.prealpha.extempdb.shared.dto.ArticleDto;
import com.prealpha.gwt.dispatch.shared.Result;

public class GetArticleResult implements Result {
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
