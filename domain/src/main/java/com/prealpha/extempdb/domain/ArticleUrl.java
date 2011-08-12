/*
 * ArticleUrl.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A canonical {@link Article} URL. Raw URLs are accepted by the constructors
 * and converted to canonical form for storage.
 * 
 * @author Meyer Kizner
 * 
 */
@Entity
public class ArticleUrl extends DistributedEntity {
	private static final long serialVersionUID = 5189190963722135652L;

	private String url;

	private transient ImmutableSet<Article> articles;

	private transient ImmutableSet<TagMapping> mappings;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected ArticleUrl() {
	}

	public ArticleUrl(String rawUrl) {
		this(null, rawUrl);
	}

	public ArticleUrl(Team creator, String rawUrl) {
		super(creator);
		Source source = Source.fromUrl(rawUrl);
		url = source.canonicalize(rawUrl);
		articles = ImmutableSet.of();
		mappings = ImmutableSet.of();
	}

	@Column(unique = true, nullable = false, updatable = false)
	public String getUrl() {
		return url;
	}

	protected void setUrl(String url) {
		checkNotNull(url);
		this.url = url;
	}

	@OneToMany(mappedBy = "url")
	protected Set<Article> getArticles() {
		return articles;
	}

	protected void setArticles(Set<Article> articles) {
		checkNotNull(articles);
		this.articles = ImmutableSet.copyOf(articles);
	}

	@Transient
	public Article getArticle() {
		Set<Article> headArticles = Sets.filter(articles,
				new Predicate<Article>() {
					@Override
					public boolean apply(Article input) {
						return (input.getChild() == null);
					}
				});
		if (headArticles.isEmpty()) {
			return null;
		} else {
			checkState(headArticles.size() == 1);
			return headArticles.iterator().next();
		}
	}

	@OneToMany(mappedBy = "articleUrl")
	public Set<TagMapping> getMappings() {
		return mappings;
	}

	protected void setMappings(Set<TagMapping> mappings) {
		checkNotNull(mappings);
		this.mappings = ImmutableSet.copyOf(mappings);
	}

	@Transient
	@Override
	public byte[] getBytes() {
		return url.getBytes(Charsets.UTF_8);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		if (!(obj instanceof ArticleUrl)) {
			return false;
		}
		ArticleUrl other = (ArticleUrl) obj;
		if (url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!url.equals(other.url)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return url;
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		setUrl(url);
	}
}
