/*
 * GetMapping.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.CacheableAction;
import com.prealpha.dispatch.shared.MergeableAction;

public class GetMapping implements CacheableAction<GetMappingResult>,
		MergeableAction<GetMappingResult> {
	private static final long EXPIRY_TIME = 1000 * 60 * 60 * 24 * 7; // 1 week

	private Long mappingId;

	private long cacheExpiry;

	// serialization support
	@SuppressWarnings("unused")
	private GetMapping() {
	}

	public GetMapping(Long mappingId) {
		checkNotNull(mappingId);
		this.mappingId = mappingId;
		cacheExpiry = System.currentTimeMillis() + EXPIRY_TIME;
	}

	public Long getMappingId() {
		return mappingId;
	}

	@Override
	public long getCacheExpiry(GetMappingResult result) {
		return cacheExpiry;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mappingId == null) ? 0 : mappingId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GetMapping)) {
			return false;
		}
		GetMapping other = (GetMapping) obj;
		if (mappingId == null) {
			if (other.mappingId != null) {
				return false;
			}
		} else if (!mappingId.equals(other.mappingId)) {
			return false;
		}
		return true;
	}
}
