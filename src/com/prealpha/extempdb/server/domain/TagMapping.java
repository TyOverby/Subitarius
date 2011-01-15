/*
 * TagMapping.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.NaturalId;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "tag_name",
		"article_id" }) })
public class TagMapping {
	public static enum State {
		PATROLLED, UNPATROLLED, REMOVED;
	}

	private Long id;

	private Tag tag;

	private Article article;

	private Date added;

	private List<TagMappingAction> actions;

	public TagMapping() {
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	@NaturalId
	@ManyToOne
	@JoinColumn(nullable = false)
	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	@NaturalId
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
