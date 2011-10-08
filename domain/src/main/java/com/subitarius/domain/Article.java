/*
 * Article.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;

@Entity
public class Article extends DistributedEntity {
	private static final long serialVersionUID = -2943998260923341509L;

	private ArticleUrl url;

	private String title;

	private String byline;

	private Date date;

	private List<String> paragraphs;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected Article() {
	}

	public Article(Team creator, ArticleUrl url, String title, String byline,
			Date articleDate, List<String> paragraphs) {
		super(creator, url);
		setUrl(url);
		setTitle(title);
		setByline(byline);
		setDate(articleDate);
		setParagraphs(paragraphs);
	}

	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	public ArticleUrl getUrl() {
		return url;
	}

	protected void setUrl(ArticleUrl url) {
		checkNotNull(url);
		this.url = url;
	}

	@Column(nullable = false, updatable = false)
	public String getTitle() {
		return title;
	}

	protected void setTitle(String title) {
		checkNotNull(title);
		this.title = title;
	}

	@Column(updatable = false)
	public String getByline() {
		return byline;
	}

	protected void setByline(String byline) {
		if (byline != null) {
			checkArgument(!byline.isEmpty());
		}
		this.byline = byline;
	}

	@Temporal(TemporalType.DATE)
	@Column(nullable = false, updatable = false)
	public Date getDate() {
		return new Date(date.getTime());
	}

	protected void setDate(Date date) {
		checkNotNull(date);
		checkArgument(date.compareTo(new Date()) <= 0);
		this.date = new Date(date.getTime());
	}

	@ElementCollection
	@Lob
	@Column(nullable = false, updatable = false)
	public List<String> getParagraphs() {
		return paragraphs;
	}

	protected void setParagraphs(List<String> paragraphs) {
		checkNotNull(paragraphs);
		checkArgument(!paragraphs.isEmpty());
		this.paragraphs = paragraphs;
	}

	@Transient
	@Override
	public byte[] getBytes() {
		byte[] urlBytes = url.getHashBytes();
		byte[] titleBytes = title.getBytes(Charsets.UTF_8);
		byte[] bylineBytes = (byline == null ? new byte[0] : byline
				.getBytes(Charsets.UTF_8));
		byte[] dateBytes = Longs.toByteArray(date.getTime());
		List<byte[]> paragraphBytes = Lists.transform(paragraphs,
				new Function<String, byte[]>() {
					@Override
					public byte[] apply(String input) {
						return input.getBytes(Charsets.UTF_8);
					}
				});
		return DistributedEntity.merge(urlBytes, titleBytes, bylineBytes,
				dateBytes, DistributedEntity.merge(paragraphBytes));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((byline == null) ? 0 : byline.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((paragraphs == null) ? 0 : paragraphs.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		if (!(obj instanceof Article)) {
			return false;
		}
		Article other = (Article) obj;
		if (byline == null) {
			if (other.byline != null) {
				return false;
			}
		} else if (!byline.equals(other.byline)) {
			return false;
		}
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (!date.equals(other.date)) {
			return false;
		}
		if (paragraphs == null) {
			if (other.paragraphs != null) {
				return false;
			}
		} else if (!paragraphs.equals(other.paragraphs)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
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

	@Override
	public String toString() {
		return String.format("%s (%s)", title, url);
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		setUrl(url);
		setTitle(title);
		setByline(byline);
		setDate(date);
		setParagraphs(paragraphs);
	}
}
