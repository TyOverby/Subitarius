/*
 * TagMappingDto.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.dto;

import static com.google.common.base.Preconditions.*;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TagMappingDto implements IsSerializable {
	/*
	 * Note that hashCode() and equals() ignore the tag name's case.
	 */
	public static final class Key implements IsSerializable {
		private String tagName;

		private Long articleId;

		// serialization support
		@SuppressWarnings("unused")
		private Key() {
		}

		public Key(String tagName, Long articleId) {
			setTagName(tagName);
			setArticleId(articleId);
		}

		public String getTagName() {
			return tagName;
		}

		public void setTagName(String tagName) {
			checkNotNull(tagName);
			this.tagName = tagName;
		}

		public Long getArticleId() {
			return articleId;
		}

		public void setArticleId(Long articleId) {
			checkNotNull(articleId);
			this.articleId = articleId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((articleId == null) ? 0 : articleId.hashCode());
			result = prime
					* result
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
			if (!(obj instanceof Key)) {
				return false;
			}
			Key other = (Key) obj;
			if (articleId == null) {
				if (other.articleId != null) {
					return false;
				}
			} else if (!articleId.equals(other.articleId)) {
				return false;
			}
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

	public static enum State {
		PATROLLED, UNPATROLLED, REMOVED;
	}

	private Key key;

	private TagDto tag;

	private ArticleDto article;

	private Date added;

	private State state;

	public TagMappingDto() {
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public TagDto getTag() {
		return tag;
	}

	public void setTag(TagDto tag) {
		this.tag = tag;
	}

	public ArticleDto getArticle() {
		return article;
	}

	public void setArticle(ArticleDto article) {
		this.article = article;
	}

	public Date getAdded() {
		return added;
	}

	public void setAdded(Date added) {
		this.added = added;
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
		result = prime * result + ((article == null) ? 0 : article.hashCode());
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
		if (article == null) {
			if (other.article != null) {
				return false;
			}
		} else if (!article.equals(other.article)) {
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
