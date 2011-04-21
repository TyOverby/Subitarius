/*
 * BingSearchResponse.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.search;

class BingSearchResponse {
	private String version;

	private BingQuery query;

	private BingNews news;

	protected BingSearchResponse() {
	}

	public String getVersion() {
		return version;
	}

	public BingQuery getQuery() {
		return query;
	}

	public BingNews getNews() {
		return news;
	}
}
