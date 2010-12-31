/*
 * BingNewsResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.search;

import java.util.Date;

class BingNewsResult {
	private String title;

	private String url;

	private String source;

	private String snippet;

	private Date date;

	private boolean breakingNews;

	protected BingNewsResult() {
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public String getSource() {
		return source;
	}

	public String getSnippet() {
		return snippet;
	}

	public Date getDate() {
		return date;
	}

	public boolean isBreakingNews() {
		return breakingNews;
	}
}
