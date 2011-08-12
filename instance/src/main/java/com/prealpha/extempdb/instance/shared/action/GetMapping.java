/*
 * GetMapping.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.filter.CacheableAction;
import com.prealpha.dispatch.shared.filter.MergeableAction;

public class GetMapping implements CacheableAction<GetMappingResult>,
		MergeableAction<GetMappingResult> {
	private static final long EXPIRY_TIME = 1000 * 60 * 60 * 24 * 7; // 1 week

	private String tagName;
	
	private String articleUrlHash;

	private long cacheExpiry;

	// serialization support
	@SuppressWarnings("unused")
	private GetMapping() {
	}

	public GetMapping(String tagName, String articleUrlHash) {
		checkNotNull(tagName);
		checkNotNull(articleUrlHash);
		this.tagName = tagName;
		this.articleUrlHash = articleUrlHash;
		cacheExpiry = System.currentTimeMillis() + EXPIRY_TIME;
	}

	public String getTagName() {
		return tagName;
	}
	
	public String getArticleUrlHash() {
		return articleUrlHash;
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
				+ ((articleUrlHash == null) ? 0 : articleUrlHash.hashCode());
		result = prime * result + ((tagName == null) ? 0 : tagName.hashCode());
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
		if (articleUrlHash == null) {
			if (other.articleUrlHash != null) {
				return false;
			}
		} else if (!articleUrlHash.equals(other.articleUrlHash)) {
			return false;
		}
		if (tagName == null) {
			if (other.tagName != null) {
				return false;
			}
		} else if (!tagName.equals(other.tagName)) {
			return false;
		}
		return true;
	}
}
