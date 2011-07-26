/*
 * TagMapping.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.google.common.base.Charsets;

@Entity
public class TagMapping extends DistributedEntity {
	public static enum State {
		STICKIED, PATROLLED, UNPATROLLED, REMOVED, ARCHIVED;
	}

	private Tag tag;

	private Article article;

	private State state;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected TagMapping() {
	}

	/**
	 * This constructor is intended for use by the searcher on the central
	 * server. It sets the state to {@link State#UNPATROLLED}.
	 * 
	 * @param tag
	 *            the tag to map
	 * @param article
	 *            the article to map
	 */
	public TagMapping(Tag tag, Article article) {
		this(null, null, tag, article, State.UNPATROLLED);
	}

	public TagMapping(User creator, TagMapping parent, Tag tag,
			Article article, State state) {
		super(creator, parent);
		setTag(tag);
		setArticle(article);
		setState(state);
	}

	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	public Tag getTag() {
		return tag;
	}

	protected void setTag(Tag tag) {
		checkNotNull(tag);
		this.tag = tag;
	}

	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	public Article getArticle() {
		return article;
	}

	protected void setArticle(Article article) {
		checkNotNull(article);
		this.article = article;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	public State getState() {
		return state;
	}

	protected void setState(State state) {
		checkNotNull(state);
		this.state = state;
	}

	@Override
	protected byte[] getBytes() {
		byte[] tagBytes = tag.getHashBytes();
		byte[] articleBytes = article.getHashBytes();
		byte[] stateBytes = state.name().getBytes(Charsets.UTF_8);
		return Hashable.merge(tagBytes, articleBytes, stateBytes);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((article == null) ? 0 : article.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		if (state != other.state) {
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

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		setTag(tag);
		setArticle(article);
		setState(state);
	}
}
