/*
 * ArticleField.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.jump;

import java.util.Comparator;

import com.prealpha.extempdb.instance.shared.dto.ArticleDto;

enum ArticleField implements Comparator<ArticleDto> {
	TITLE {
		@Override
		public String getField(ArticleDto article) {
			return article.getTitle();
		}
	},

	DATE {
		@Override
		public String getField(ArticleDto article) {
			return article.getDate();
		}
	},

	SOURCE {
		@Override
		public String getField(ArticleDto article) {
			return article.getUrl().getSource();
		}
	};

	public abstract String getField(ArticleDto article);

	@Override
	public int compare(ArticleDto a1, ArticleDto a2) {
		return getField(a1).compareTo(getField(a2));
	}

	@Override
	public String toString() {
		String name = super.toString().toLowerCase();
		char initial = name.charAt(0);
		initial = Character.toUpperCase(initial);
		return initial + name.substring(1);
	}
}
