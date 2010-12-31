/*
 * ArticleField.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.browse;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.prealpha.extempdb.shared.dto.ArticleDto;

enum ArticleField implements Comparator<ArticleDto> {
	ID {
		@Override
		public String getField(ArticleDto article) {
			return article.getId().toString();
		}

		@Override
		public int compare(ArticleDto a1, ArticleDto a2) {
			return a1.getId().compareTo(a2.getId());
		}

		@Override
		public String toString() {
			return name();
		}
	},

	TITLE {
		@Override
		public String getField(ArticleDto article) {
			return article.getTitle();
		}
	},

	DATE {
		private final List<String> months = Arrays.asList("Jan", "Feb", "Mar",
				"Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

		@Override
		public String getField(ArticleDto article) {
			return format(article.getDate());
		}

		@SuppressWarnings("deprecation")
		private String format(Date date) {
			// TODO: hacky; needs to work on both client and server, and in UTC
			// it's probably better to somehow avoid serializing a comparator
			String gmtStr = date.toGMTString();
			String[] tokens = gmtStr.split(" ");
			int day = Integer.parseInt(tokens[0]);
			int month = getMonth(tokens[1]);
			int year = Integer.parseInt(tokens[2]);
			String dateStr = pad(year, 4) + '-' + pad(month, 2) + '-'
					+ pad(day, 2);
			return dateStr;
		}

		private String pad(int num, int size) {
			String str = Integer.toString(num);
			while (str.length() < size) {
				str = '0' + str;
			}
			return str;
		}

		private int getMonth(String monthAbbr) {
			int index = months.indexOf(monthAbbr);
			if (index >= 0) {
				return index + 1;
			} else {
				throw new IllegalArgumentException();
			}
		}
	},

	SOURCE {
		@Override
		public String getField(ArticleDto article) {
			return article.getSource().getDisplayName();
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
