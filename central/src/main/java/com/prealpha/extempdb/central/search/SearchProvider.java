/*
 * SearchProvider.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central.search;

import java.util.List;

interface SearchProvider {
	List<String> search(SearchQuery query) throws SearchUnavailableException;

	List<String> search(SearchQuery query, int limit)
			throws SearchUnavailableException;
}
