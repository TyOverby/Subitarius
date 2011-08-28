/*
 * ArticleSort.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.jump;

import static com.google.common.base.Preconditions.*;

import java.util.Comparator;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.subitarius.instance.shared.dto.ArticleDto;

class ArticleSort implements Comparator<ArticleDto>, IsSerializable {
	public static final ArticleSort DEFAULT_SORT = new ArticleSort(
			ArticleField.DATE, false, null);

	private ArticleField field;

	private boolean ascending;

	private ArticleSort lastSort;

	// serialization support
	@SuppressWarnings("unused")
	private ArticleSort() {
	}

	public ArticleSort(ArticleField field, boolean ascending,
			ArticleSort lastSort) {
		checkNotNull(field);

		this.field = field;
		this.ascending = ascending;

		while (lastSort != null && lastSort.getField().equals(field)) {
			lastSort = lastSort.getLastSort();
		}

		this.lastSort = lastSort;
	}

	public ArticleField getField() {
		return field;
	}

	public boolean isAscending() {
		return ascending;
	}

	public ArticleSort getLastSort() {
		return lastSort;
	}

	@Override
	public int compare(ArticleDto a1, ArticleDto a2) {
		int result;

		if (a1 == null) {
			if (a2 == null) {
				result = 0;
			} else {
				result = Integer.MIN_VALUE;
			}
		} else if (a2 == null) {
			result = Integer.MAX_VALUE;
		} else {
			if (ascending) {
				result = field.compare(a1, a2);
			} else {
				result = field.compare(a2, a1);
			}
		}

		if (result == 0 && lastSort != null) {
			return lastSort.compare(a1, a2);
		} else {
			return result;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ascending ? 1231 : 1237);
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result
				+ ((lastSort == null) ? 0 : lastSort.hashCode());
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
		if (!(obj instanceof ArticleSort)) {
			return false;
		}
		ArticleSort other = (ArticleSort) obj;
		if (ascending != other.ascending) {
			return false;
		}
		if (field != other.field) {
			return false;
		}
		if (lastSort == null) {
			if (other.lastSort != null) {
				return false;
			}
		} else if (!lastSort.equals(other.lastSort)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ArticleSort [field=" + field + ", ascending=" + ascending
				+ ", lastSort=" + lastSort + "]";
	}
}
