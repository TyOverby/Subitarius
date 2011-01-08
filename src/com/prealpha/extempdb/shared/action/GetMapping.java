/*
 * GetMapping.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.CacheableAction;
import com.prealpha.dispatch.shared.MergeableAction;
import com.prealpha.extempdb.shared.id.TagMappingId;

public class GetMapping implements CacheableAction<GetMappingResult>,
		MergeableAction<GetMappingResult> {
	private static final long EXPIRY_TIME = 1000 * 60 * 60 * 24 * 7; // 1 week

	private TagMappingId id;

	private long cacheExpiry;

	// serialization support
	@SuppressWarnings("unused")
	private GetMapping() {
	}

	public GetMapping(TagMappingId id) {
		checkNotNull(id);
		this.id = id;
		cacheExpiry = System.currentTimeMillis() + EXPIRY_TIME;
	}

	public TagMappingId getId() {
		return id;
	}

	@Override
	public long getCacheExpiry(GetMappingResult result) {
		return cacheExpiry;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
