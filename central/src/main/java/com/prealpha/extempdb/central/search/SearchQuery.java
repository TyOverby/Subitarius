/*
 * SearchQuery.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central.search;

import static com.google.common.base.Preconditions.*;

import com.prealpha.extempdb.domain.Source;
import com.prealpha.extempdb.domain.Tag;

final class SearchQuery {
	private final Source source;

	private final Tag tag;

	public SearchQuery(Source source, Tag tag) {
		checkNotNull(source);
		checkNotNull(tag);
		this.source = source;
		this.tag = tag;
	}

	public Source getSource() {
		return source;
	}

	public Tag getTag() {
		return tag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
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
		if (!(obj instanceof SearchQuery)) {
			return false;
		}
		SearchQuery other = (SearchQuery) obj;
		if (source == null) {
			if (other.source != null) {
				return false;
			}
		} else if (!source.equals(other.source)) {
			return false;
		}
		if (tag == null) {
			if (other.tag != null) {
				return false;
			}
		} else if (!tag.equals(other.tag)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("SearchQuery[source=\"%s\", tag=\"%s\"]", source,
				tag.getName());
	}
}
