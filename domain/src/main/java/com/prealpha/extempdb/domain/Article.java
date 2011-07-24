/*
 * Article.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

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

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;

@Entity
public class Article extends DistributedEntity {
	private ArticleUrl url;

	private String title;

	private String byline;

	private Date articleDate;

	private Date parseDate;

	private ImmutableList<String> paragraphs;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected Article() {
	}

	public Article(User creator, DistributedEntity parent, ArticleUrl url,
			String title, String byline, Date articleDate,
			List<String> paragraphs) {
		super(creator, parent);
		setUrl(url);
		setTitle(title);
		setByline(byline);
		setArticleDate(articleDate);
		setParseDate(new Date());
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
		if (byline != null) {
			checkArgument(!byline.isEmpty());
		}
		return byline;
	}

	protected void setByline(String byline) {
		this.byline = byline;
	}

	@Temporal(TemporalType.DATE)
	@Column(nullable = false, updatable = false)
	public Date getArticleDate() {
		return new Date(articleDate.getTime());
	}

	protected void setArticleDate(Date articleDate) {
		checkNotNull(articleDate);
		checkArgument(articleDate.compareTo(new Date()) <= 0);
		checkArgument(articleDate.compareTo(url.getSearchDate()) <= 0);
		this.articleDate = new Date(articleDate.getTime());
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = false)
	public Date getParseDate() {
		return new Date(parseDate.getTime());
	}

	protected void setParseDate(Date parseDate) {
		checkNotNull(parseDate);
		checkArgument(parseDate.compareTo(new Date()) <= 0);
		checkArgument(parseDate.compareTo(url.getSearchDate()) >= 0);
		this.parseDate = new Date(parseDate.getTime());
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
		this.paragraphs = ImmutableList.copyOf(paragraphs);
	}

	@Override
	protected byte[] getBytes() {
		byte[] urlBytes = url.getBytes();
		byte[] titleBytes = title.getBytes(Charsets.UTF_8);
		byte[] bylineBytes = (byline == null ? new byte[0] : byline
				.getBytes(Charsets.UTF_8));
		byte[] articleDateBytes = Longs.toByteArray(articleDate.getTime());
		List<byte[]> paragraphBytes = Lists.transform(paragraphs,
				new Function<String, byte[]>() {
					@Override
					public byte[] apply(String input) {
						return input.getBytes(Charsets.UTF_8);
					}
				});
		return Hashable.merge(urlBytes, titleBytes, bylineBytes,
				articleDateBytes, merge(paragraphBytes));
	}

	/**
	 * Implements {@link Hashable#merge(byte[]...)} for a list of byte arrays,
	 * for use in converting {@link #paragraphs} to a single byte array.
	 * 
	 * @param byteList
	 *            a list of byte arrays
	 * @return a single byte array representing the entire list concatenated
	 *         together, with null bytes between each entry in the list
	 */
	private static byte[] merge(List<byte[]> byteList) {
		int totalLength = 0;
		for (byte[] array : byteList) {
			totalLength += array.length;
		}
		byte[] merged = new byte[totalLength + byteList.size()];
		int pos = 0;
		for (byte[] array : byteList) {
			System.arraycopy(array, 0, merged, pos, array.length);
			merged[pos + array.length] = 0x00;
			pos += array.length + 1;
		}
		return merged;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((articleDate == null) ? 0 : articleDate.hashCode());
		result = prime * result + ((byline == null) ? 0 : byline.hashCode());
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
		if (articleDate == null) {
			if (other.articleDate != null) {
				return false;
			}
		} else if (!articleDate.equals(other.articleDate)) {
			return false;
		}
		if (byline == null) {
			if (other.byline != null) {
				return false;
			}
		} else if (!byline.equals(other.byline)) {
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

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		setUrl(url);
		setTitle(title);
		setByline(byline);
		setArticleDate(articleDate);
		setParseDate(parseDate);
		setParagraphs(paragraphs);
	}
}
