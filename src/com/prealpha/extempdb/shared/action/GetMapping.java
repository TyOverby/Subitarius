/*
 * GetMapping.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.CacheableAction;
import com.prealpha.dispatch.shared.MergeableAction;
import com.prealpha.extempdb.shared.dto.TagMappingDto;

public class GetMapping implements CacheableAction<GetMappingResult>,
		MergeableAction<GetMappingResult> {
	private static final long EXPIRY_TIME = 1000 * 60 * 60 * 24 * 7; // 1 week

	private TagMappingDto.Key mappingKey;

	private long cacheExpiry;

	// serialization support
	@SuppressWarnings("unused")
	private GetMapping() {
	}

	public GetMapping(TagMappingDto.Key mappingKey) {
		checkNotNull(mappingKey);
		this.mappingKey = mappingKey;
		cacheExpiry = System.currentTimeMillis() + EXPIRY_TIME;
	}

	public TagMappingDto.Key getMappingKey() {
		return mappingKey;
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
				+ ((mappingKey == null) ? 0 : mappingKey.hashCode());
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
		if (mappingKey == null) {
			if (other.mappingKey != null) {
				return false;
			}
		} else if (!mappingKey.equals(other.mappingKey)) {
			return false;
		}
		return true;
	}
}
