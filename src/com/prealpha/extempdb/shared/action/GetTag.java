/*
 * GetTag.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.Date;

import com.prealpha.extempdb.shared.id.TagName;
import com.prealpha.gwt.dispatch.client.filter.CacheableAction;
import com.prealpha.gwt.dispatch.client.filter.MergeableAction;

public class GetTag implements CacheableAction<GetTagResult>,
		MergeableAction<GetTagResult> {
	private static final long EXPIRY_TIME = 1000 * 60 * 60 * 24 * 7; // 1 week

	private TagName name;

	private long cacheExpiry;

	// serialization support
	@SuppressWarnings("unused")
	private GetTag() {
	}

	public GetTag(TagName name) {
		checkNotNull(name);
		this.name = name;
		cacheExpiry = System.currentTimeMillis() + EXPIRY_TIME;
	}

	public TagName getName() {
		return name;
	}

	@Override
	public Date getCacheExpiry(GetTagResult result) {
		return new Date(cacheExpiry);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof GetTag)) {
			return false;
		}
		GetTag other = (GetTag) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
