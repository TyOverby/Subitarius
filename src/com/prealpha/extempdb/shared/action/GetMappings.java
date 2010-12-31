/*
 * GetMappings.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import java.util.Date;

import com.google.common.base.Predicate;
import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.prealpha.gwt.dispatch.client.filter.CacheableAction;
import com.prealpha.gwt.dispatch.client.filter.MergeableAction;

/*
 * TODO: the caching behavior is not ideal. Needs to be sensitive to mutation.
 */
abstract class GetMappings implements CacheableAction<GetMappingsResult>,
		MergeableAction<GetMappingsResult>, Predicate<TagMappingDto> {
	private static final long EXPIRY_INTERVAL = 1000 * 60 * 60 * 24; // 24 hours

	private long cacheExpiry;

	protected GetMappings() {
		// we want the next midnight UTC after the current time
		cacheExpiry = (System.currentTimeMillis() / EXPIRY_INTERVAL)
				* EXPIRY_INTERVAL + EXPIRY_INTERVAL;
	}

	@Override
	public Date getCacheExpiry(GetMappingsResult result) {
		return new Date(cacheExpiry);
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GetMappings)) {
			return false;
		}
		return true;
	}
}
