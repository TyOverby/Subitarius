/*
 * GetTag.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.filter.CacheableAction;
import com.prealpha.dispatch.shared.filter.MergeableAction;

/*
 * Note that hashCode() and equals() ignore the tag name's case.
 */
public final class GetTag implements CacheableAction<GetTagResult>,
		MergeableAction<GetTagResult> {
	private static final long EXPIRY_TIME = 1000 * 60 * 60 * 24 * 7; // 1 week

	private String tagName;

	private long cacheExpiry;

	// serialization support
	@SuppressWarnings("unused")
	private GetTag() {
	}

	public GetTag(String tagName) {
		checkNotNull(tagName);
		this.tagName = tagName;
		cacheExpiry = System.currentTimeMillis() + EXPIRY_TIME;
	}

	public String getTagName() {
		return tagName;
	}

	@Override
	public long getCacheExpiry(GetTagResult result) {
		return cacheExpiry;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((tagName == null) ? 0 : tagName.toUpperCase().hashCode());
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
		if (tagName == null) {
			if (other.tagName != null) {
				return false;
			}
		} else if (!tagName.equalsIgnoreCase(other.tagName)) {
			return false;
		}
		return true;
	}
}
