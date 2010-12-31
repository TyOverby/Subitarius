/*
 * GetHierarchy.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import java.util.Date;

import com.prealpha.gwt.dispatch.client.filter.CacheableAction;
import com.prealpha.gwt.dispatch.client.filter.MergeableAction;

public class GetHierarchy implements CacheableAction<GetHierarchyResult>,
		MergeableAction<GetHierarchyResult> {
	private static final long EXPIRY_TIME = 1000 * 60 * 60 * 24 * 7; // 1 week

	private long cacheExpiry;

	public GetHierarchy() {
		cacheExpiry = System.currentTimeMillis() + EXPIRY_TIME;
	}

	@Override
	public Date getCacheExpiry(GetHierarchyResult result) {
		return new Date(cacheExpiry);
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (!(obj instanceof GetHierarchy)) {
			return false;
		} else {
			return true;
		}
	}
}
