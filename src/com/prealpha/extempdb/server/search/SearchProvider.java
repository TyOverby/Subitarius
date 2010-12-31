/*
 * SearchProvider.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.search;

import java.util.List;

import com.prealpha.extempdb.server.domain.Source;
import com.prealpha.extempdb.server.domain.Tag;

public interface SearchProvider {
	List<String> search(Tag tag, Source source)
			throws SearchUnavailableException;
}
