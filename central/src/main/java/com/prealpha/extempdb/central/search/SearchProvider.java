/*
 * SearchProvider.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central.search;

import java.util.List;

import com.prealpha.extempdb.domain.ArticleUrl;
import com.prealpha.extempdb.domain.Source;
import com.prealpha.extempdb.domain.Tag;

interface SearchProvider {
	List<ArticleUrl> search(Tag tag, Source source)
			throws SearchUnavailableException;

	List<ArticleUrl> search(Tag tag, Source source, int limit)
			throws SearchUnavailableException;
}
