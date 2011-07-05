/*
 * TagMapping.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
public class TagMapping {
	/*
	 * Note that hashCode() and equals() ignore the tag name's case.
	 */
	@Embeddable
	public static final class Key implements Serializable {
		private static final long serialVersionUID = 1139347306367529620L;

		private String tagName;

		private Long articleId;

		// persistence support
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

		@Override
		public String toString() {
			return String.format("{ %s; %d }", tagName, articleId);
		}

		private void readObject(ObjectInputStream ois) throws IOException,
				ClassNotFoundException {
			ois.defaultReadObject();

			if (tagName == null || articleId == null) {
				throw new InvalidObjectException("null instance field");
			}
		}
	}

	public static enum State {
		PATROLLED, UNPATROLLED, REMOVED;
	}

	private Key key;

	private Tag tag;

	private Article article;

	private Date added;

	private List<TagMappingAction> actions;

	public TagMapping() {
	}

	@EmbeddedId
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	@MapsId("tagName")
	@ManyToOne
	@JoinColumn(nullable = false)
	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	@MapsId("articleId")
	@ManyToOne
	@JoinColumn(nullable = false)
	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getAdded() {
		return added;
	}

	public void setAdded(Date added) {
		this.added = added;
	}

	@OneToMany(mappedBy = "mapping")
	@OrderBy("timestamp")
	public List<TagMappingAction> getActions() {
		return actions;
	}

	public void setActions(List<TagMappingAction> actions) {
		this.actions = actions;
	}

	@Transient
	public TagMappingAction getLastAction() {
		return (actions.isEmpty() ? null : actions.get(actions.size() - 1));
	}

	@Transient
	public State getState() {
		if (actions.isEmpty()) {
			return State.UNPATROLLED;
		} else {
			switch (getLastAction().getType()) {
			case PATROL:
				return State.PATROLLED;
			case REMOVE:
				return State.REMOVED;
			default:
				throw new IllegalStateException();
			}
		}
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
		if (!(obj instanceof TagMapping)) {
			return false;
		}
		TagMapping other = (TagMapping) obj;
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
