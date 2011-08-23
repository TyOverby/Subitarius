/*
 * ArticleParseException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import static com.google.common.base.Preconditions.checkNotNull;

import com.prealpha.extempdb.domain.ArticleUrl;

public class ArticleParseException extends Exception {
	private static final long serialVersionUID = 1L;

	private final ArticleUrl articleUrl;

	public ArticleParseException(ArticleUrl articleUrl) {
		this(articleUrl, null);
	}

	public ArticleParseException(ArticleUrl articleUrl, Throwable cause) {
		super("raised an exception while parsing article, URL " + articleUrl,
				cause);
		checkNotNull(articleUrl);
		this.articleUrl = articleUrl;
	}

	public ArticleUrl getArticleUrl() {
		return articleUrl;
	}
}
