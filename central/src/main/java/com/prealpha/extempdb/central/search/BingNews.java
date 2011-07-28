/*
 * BingNews.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central.search;

import java.util.List;

final class BingNews {
	private int total;

	private int offset;

	private List<BingNewsResult> results;

	protected BingNews() {
	}

	public int getTotal() {
		return total;
	}

	public int getOffset() {
		return offset;
	}

	public List<BingNewsResult> getResults() {
		return results;
	}
}
