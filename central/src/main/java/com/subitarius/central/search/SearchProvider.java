/*
 * SearchProvider.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.central.search;

import java.util.List;

import com.subitarius.domain.ArticleUrl;
import com.subitarius.domain.Source;
import com.subitarius.domain.Tag;

interface SearchProvider {
	List<ArticleUrl> search(Tag tag, Source source)
			throws SearchUnavailableException;

	List<ArticleUrl> search(Tag tag, Source source, int limit)
			throws SearchUnavailableException;
}
