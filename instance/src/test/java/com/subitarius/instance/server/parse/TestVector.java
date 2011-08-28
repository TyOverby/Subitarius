/*
 * TestVector.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.parse;

import static com.google.common.base.Preconditions.*;

import java.util.Date;
import java.util.List;

import com.subitarius.domain.Article;
import com.subitarius.domain.ArticleUrl;

public final class TestVector {
	final String url;

	final Article article;

	public TestVector(String url) {
		checkNotNull(url);
		this.url = url;
		article = null;
	}

	public TestVector(String url, String title, String byline, Date date,
			List<String> paragraphs) {
		checkNotNull(url);
		checkNotNull(title);
		checkNotNull(date);
		checkNotNull(paragraphs);
		checkArgument(paragraphs.size() > 0);
		this.url = url;
		article = new Article(null, new ArticleUrl(url), title, byline, date,
				paragraphs);
	}
}
