/*
 * NyTimesArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.prealpha.extempdb.util.http.RobotsExclusionException;
import com.prealpha.extempdb.util.http.SimpleHttpClient;

final class NyTimesArticleParser extends AbstractArticleParser {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");

	private static final List<String> UNPARSEABLE_TYPES = ImmutableList.of(
			"Interactive", "blog post", "Subject", "Gift Guide", "Login");

	private final SimpleHttpClient httpClient;

	@Inject
	private NyTimesArticleParser(SimpleHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public ProtoArticle parse(String url) throws ArticleParseException {
		try {
			Map<String, String> params = Collections.singletonMap("pagewanted",
					"all");
			InputStream stream = httpClient.doGet(url, params);
			Document document = Jsoup.parse(stream, null, url);

			// they are evil and sometimes give us an ad page
			// fortunately, the ads have a meta tag that gives us a URL
			String urlContent = document.select("meta[http-equiv=refresh]")
					.attr("content");
			if (!urlContent.isEmpty()) {
				String urlFragment = urlContent.split(";")[1];
				return parse("http://www.nytimes.com" + urlFragment);
			}

			String pageType = document.select("meta[name=PST]").attr("content");
			if (UNPARSEABLE_TYPES.contains(pageType)) {
				return null;
			}

			String title = document.select("meta[name=hdl]").attr("content");
			String byline = document.select("meta[name=byl]").attr("content");
			Date date;

			try {
				String dateStr = document.select("meta[name=pdate]").attr(
						"content");
				date = DATE_FORMAT.parse(dateStr);
			} catch (ParseException px) {
				throw new ArticleParseException(url, px);
			}

			List<String> paragraphs = Lists.newArrayList();
			List<Element> elements = document.select("div.articleBody p");
			for (Element elem : elements) {
				String text = elem.text().trim();
				// NY Times uses U+0095 as a dot, which MySQL doesn't like
				text = text.replace('\u0095', '\u2022');
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
