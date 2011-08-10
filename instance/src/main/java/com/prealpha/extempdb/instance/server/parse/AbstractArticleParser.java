/*
 * AbstractArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

abstract class AbstractArticleParser implements ArticleParser {
	protected AbstractArticleParser() {
	}

	@Override
	public String getCanonicalUrl(String url) {
		int parameterIndex = url.indexOf('?');

		if (parameterIndex >= 0) {
			return url.substring(0, parameterIndex);
		} else {
			return url;
		}
	}
}
