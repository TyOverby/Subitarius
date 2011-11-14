/*
 * AddArticle.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.xylophone.shared.Action;

public final class AddArticle implements Action<AddArticleResult> {
	private String url;

	// serialization support
	@SuppressWarnings("unused")
	private AddArticle() {
	}

	public AddArticle(String url) {
		checkNotNull(url);
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
