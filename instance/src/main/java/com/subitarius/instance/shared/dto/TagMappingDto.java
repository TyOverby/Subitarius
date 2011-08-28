/*
 * TagMappingDto.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TagMappingDto implements IsSerializable {
	public static enum State {
		STICKIED, PATROLLED, UNPATROLLED, REMOVED, ARCHIVED;
	}

	private String hash;

	private TagDto tag;

	private ArticleUrlDto articleUrl;

	private State state;

	public TagMappingDto() {
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public TagDto getTag() {
		return tag;
	}

	public void setTag(TagDto tag) {
		this.tag = tag;
	}

	public ArticleUrlDto getArticleUrl() {
		return articleUrl;
	}

	public void setArticle(ArticleUrlDto articleUrl) {
		this.articleUrl = articleUrl;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((articleUrl == null) ? 0 : articleUrl.hashCode());
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
		if (!(obj instanceof TagMappingDto)) {
			return false;
		}
		TagMappingDto other = (TagMappingDto) obj;
		if (articleUrl == null) {
			if (other.articleUrl != null) {
				return false;
			}
		} else if (!articleUrl.equals(other.articleUrl)) {
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
}
