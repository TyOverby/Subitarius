/*
 * GetMappings.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import com.google.common.base.Predicate;
import com.prealpha.dispatch.shared.filter.CacheableAction;
import com.prealpha.dispatch.shared.filter.MergeableAction;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;

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
	public long getCacheExpiry(GetMappingsResult result) {
		return cacheExpiry;
	}
}
