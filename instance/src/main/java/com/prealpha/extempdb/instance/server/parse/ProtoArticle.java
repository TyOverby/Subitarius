/*
 * ProtoArticle.java
 * Copyright (C) 2011 Meyer Kizner, Ty Overby
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import static com.google.common.base.Preconditions.*;

import java.util.Date;
import java.util.List;

import com.prealpha.extempdb.instance.domain.Article;

public final class ProtoArticle {
	private final String title;

	private final String byline;

	private final Date date;

	private final List<String> paragraphs;

	public ProtoArticle(String title, String byline, Date date,
			List<String> paragraphs) {
		checkNotNull(title);
		checkNotNull(date);
		checkNotNull(paragraphs);

		this.title = title;
		this.byline = byline;
		this.date = date;
		this.paragraphs = paragraphs;
	}

	public void fill(Article article) {
		article.setTitle(title);
		article.setByline(byline);
		article.setDate(date);
		article.setParagraphs(paragraphs);
	}

	public String getTitle() {
		return title;
	}

	public String getByline() {
		return byline;
	}

	public Date getDate() {
		return date;
	}

	public List<String> getParagraphs() {
		return paragraphs;
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
		if (!(obj instanceof ProtoArticle)) {
			return false;
		}
		ProtoArticle other = (ProtoArticle) obj;
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
		return true;
	}

	@Override
	public String toString() {
		String toReturn = "";
		toReturn += "Title: " + getTitle() + "\n";
		toReturn += "Byline: " + getByline() + "\n";
		toReturn += "Date: " + getDate() + "\n";
		toReturn += "Paragraphs:\n";
		for (String paragraph : getParagraphs()) {
			toReturn += paragraph + "\n\n";
		}

		return toReturn;
	}
}
