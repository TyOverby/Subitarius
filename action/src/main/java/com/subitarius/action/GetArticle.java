/*
 * GetArticle.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.action;

import com.prealpha.dispatch.shared.filter.CacheableAction;
import com.prealpha.dispatch.shared.filter.MergeableAction;

abstract class GetArticle implements CacheableAction<GetArticleResult>,
		MergeableAction<GetArticleResult> {
	protected GetArticle() {
	}

	@Override
	public long getCacheExpiry(GetArticleResult result) {
		// cache indefinitely, hashes will never change
		return Long.MAX_VALUE;
	}
}
