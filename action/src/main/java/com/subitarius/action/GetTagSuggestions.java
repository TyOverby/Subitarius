/*
 * GetTagSuggestions.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.filter.MergeableAction;

/*
 * Note that hashCode() and equals() ignore the tag name's case.
 */
public final class GetTagSuggestions implements
		MergeableAction<GetTagSuggestionsResult> {
	private String namePrefix;

	private int limit;

	// serialization support
	@SuppressWarnings("unused")
	private GetTagSuggestions() {
	}

	public GetTagSuggestions(String namePrefix, int limit) {
		checkNotNull(namePrefix);
		checkArgument(limit >= 0);
		this.namePrefix = namePrefix;
		this.limit = limit;
	}

	public String getNamePrefix() {
		return namePrefix;
	}

	public int getLimit() {
		return limit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + limit;
		result = prime
				* result
				+ ((namePrefix == null) ? 0 : namePrefix.toUpperCase()
						.hashCode());
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
		if (!(obj instanceof GetTagSuggestions)) {
			return false;
		}
		GetTagSuggestions other = (GetTagSuggestions) obj;
		if (limit != other.limit) {
			return false;
		}
		if (namePrefix == null) {
			if (other.namePrefix != null) {
				return false;
			}
		} else if (!namePrefix.equalsIgnoreCase(other.namePrefix)) {
			return false;
		}
		return true;
	}
}
