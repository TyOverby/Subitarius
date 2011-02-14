/*
 * SearchQuery.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.search;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.Date;

import com.prealpha.extempdb.server.domain.Article;
import com.prealpha.extempdb.server.domain.Source;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.domain.TagMapping;
import com.prealpha.extempdb.server.domain.TagMappingAction;

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

	public TagMapping createTagMapping(Article article) {
		checkNotNull(article);
		TagMapping mapping = new TagMapping();
		TagMapping.Key mappingKey = new TagMapping.Key(tag.getName(),
				article.getId());
		mapping.setKey(mappingKey);
		mapping.setTag(tag);
		mapping.setArticle(article);
		mapping.setAdded(new Date());
		mapping.setActions(Collections.<TagMappingAction> emptyList());
		return mapping;
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
		return String.format("SearchQuery[source=\"%s\", tag=\"%s\"]",
				source.getDisplayName(), tag.getName());
	}
}
