/*
 * GuardianArticleParser.java
 * Copyright (C) 2011 Meyer Kizner, Ty Overby
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.prealpha.extempdb.util.http.RobotsExclusionException;
import com.prealpha.extempdb.util.http.SimpleHttpClient;

final class GuardianArticleParser extends AbstractArticleParser {
	private static enum Feed {
		NONE(null, DATE_FORMAT_UK) {
		},

		AP("AP foreign", DATE_FORMAT_US) {
		},

		REUTERS("Reuters", DATE_FORMAT_US, "div#article-body-blocks > div") {
		},

		PRESS_ASSOCIATION("Press Association", DATE_FORMAT_US) {
		};

		private final String name;

		private final DateFormat dateFormat;

		private final String bodySelector;

		private Feed(String name, DateFormat dateFormat) {
			this(name, dateFormat, "div#article-body-blocks > p");
		}

		private Feed(String name, DateFormat dateFormat, String bodySelector) {
			this.name = name;
			this.dateFormat = dateFormat;
			this.bodySelector = bodySelector;
		}

		private static Feed fromName(String name) {
			for (Feed feed : values()) {
				if (name.equals(feed.name)) {
					return feed;
				}
			}
			return NONE;
		}
	}

	private static final DateFormat DATE_FORMAT_UK = new SimpleDateFormat(
			"EEEEE d MMMMM yyyy");

	private static final DateFormat DATE_FORMAT_US = new SimpleDateFormat(
			"EEEEE MMMMM d yyyy");

	private final SimpleHttpClient httpClient;

	@Inject
	private GuardianArticleParser(SimpleHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public ProtoArticle parse(String url) throws ArticleParseException {
		try {
			Map<String, String> params = Collections.emptyMap();
			InputStream stream = httpClient.doGet(url, params);
			Document document = Jsoup.parse(stream, null, url);

			if (document.body().className().contains("has-badge")) {
				// blog posts, contests, etc. all seem to contain this
				return null;
			}

			String title = document.select("h1").first().text();

			Element bylineElem = document.select("a.contributor").first();
			String byline = ((bylineElem == null) ? null : bylineElem.text());

			// get both the date and the feed from the publication element
			Date date;
			String[] publication = document.select("li.publication").first()
					.text().split(",");
			Feed feed = Feed.fromName(publication[0].trim());
			try {
				String dateStr = publication[1].trim();
				date = feed.dateFormat.parse(dateStr);
			} catch (ParseException px) {
				throw new ArticleParseException(url, px);
			}

			List<String> paragraphs = Lists.newArrayList();
			List<Element> elements = document.select(feed.bodySelector);
			for (Element elem : elements) {
				String text = elem.text().trim();
				if (!text.isEmpty()) {
					paragraphs.add(text);
				}
			}

			return new ProtoArticle(title, byline, date, paragraphs);
		} catch (IOException iox) {
			throw new ArticleParseException(url, iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(url, rex);
		}
	}
}
