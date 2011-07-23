/*
 * ArticleUrl.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;

@Entity
public class ArticleUrl extends DeltaEntity {
	private String url;

	private Source source;

	private Date searchDate;

	private ImmutableSet<TagMapping> mappings;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected ArticleUrl() {
	}

	public ArticleUrl(User creator, String url, Source source) {
		super(creator);
		checkNotNull(url);
		checkNotNull(source);
		this.url = url;
		this.source = source;
		searchDate = new Date();
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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	public Source getSource() {
		return source;
	}

	protected void setSource(Source source) {
		checkNotNull(source);
		this.source = source;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = false)
	public Date getSearchDate() {
		return new Date(searchDate.getTime());
	}

	protected void setSearchDate(Date searchDate) {
		checkNotNull(searchDate);
		checkArgument(searchDate.compareTo(new Date()) <= 0);
		this.searchDate = new Date(searchDate.getTime());
	}

	@OneToMany(mappedBy = "article")
	public Set<TagMapping> getMappings() {
		return mappings;
	}

	protected void setMappings(Set<TagMapping> mappings) {
		checkNotNull(mappings);
		this.mappings = ImmutableSet.copyOf(mappings);
	}

	@Override
	protected byte[] getBytes() {
		byte[] urlData = url.getBytes(Charsets.UTF_8);
		byte[] sourceData = source.name().getBytes(Charsets.UTF_8);
		return Hashable.merge(urlData, sourceData);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		if (source != other.source) {
			return false;
		}
		if (url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!url.equals(other.url)) {
			return false;
		}
		return true;
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		setUrl(url);
		setSource(source);
		setSearchDate(searchDate);
		setMappings(mappings);
	}
}
