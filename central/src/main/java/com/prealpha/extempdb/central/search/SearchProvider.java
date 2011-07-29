/*
 * SearchProvider.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central.search;

import java.util.List;

import com.prealpha.extempdb.domain.ArticleUrl;

interface SearchProvider {
	List<ArticleUrl> search(SearchQuery query) throws SearchUnavailableException;

	List<ArticleUrl> search(SearchQuery query, int limit)
			throws SearchUnavailableException;
}
