/*
 * WsjArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.prealpha.simplehttp.SimpleHttpClient;
import com.prealpha.simplehttp.SimpleHttpException;

final class WsjArticleParser extends AbstractArticleParser {
	private static enum ArticleType {
		COMPLETE("div.articlePage > p") {
		},

		ABRIDGED("div.article > p") {
		};

		private final String bodySelector;

		private ArticleType(String bodySelector) {
			this.bodySelector = bodySelector;
		}
	}

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"MMMMM d, yyyy");

	private static final Pattern URL_REGEX = Pattern
			.compile("http://online\\.wsj\\.com/article/SB[0-9]{41}\\.html");

	private static final List<String> UNPARSEABLE_TYPES = ImmutableList.of(
			"Letters", "Journal Concierge", "Spanish");

	private final SimpleHttpClient httpClient;

	@Inject
	private WsjArticleParser(SimpleHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public ProtoArticle parse(String url) throws ArticleParseException {
		if (!URL_REGEX.matcher(url).matches()) {
			// articles that don't match tend to disappear
			return null;
		}

		try {
			Map<String, String> params = Collections.emptyMap();
			InputStream stream = httpClient.doGet(url, params);
			Document document = Jsoup.parse(stream, null, url);

			// verify that we have a parseable article
			String pageType = document.select("meta[name=displayName]").attr(
					"content");
			if (UNPARSEABLE_TYPES.contains(pageType)) {
				return null;
			}

			// get the article type, needed later
			ArticleType type;
			if (document.select("meta[name=GOOGLEBOT]").isEmpty()) {
				type = ArticleType.COMPLETE;
			} else {
				type = ArticleType.ABRIDGED;
			}

			String title = document.select("h1").first().text();

			Element bylineElem = document.select("h3.byline").first();
			String byline;
			if (bylineElem == null) {
				byline = null;
			} else {
				byline = bylineElem.text();
				if (byline.isEmpty()) {
					byline = null;
				}
			}

			Date date;
			try {
				String dateStr = document.select("li.dateStamp").first().text();
				date = DATE_FORMAT.parse(dateStr);
			} catch (ParseException px) {
				throw new ArticleParseException(url, px);
			}

			List<String> paragraphs = Lists.newArrayList();
			List<Element> elements = document.select(type.bodySelector);
			for (Element elem : elements) {
				String text = elem.text().trim();
				if (!text.isEmpty()) {
					paragraphs.add(text);
				}
			}

			return new ProtoArticle(title, byline, date, paragraphs);
		} catch (IOException iox) {
			throw new ArticleParseException(url, iox);
		} catch (SimpleHttpException shx) {
			throw new ArticleParseException(url, shx);
		}
	}
}
