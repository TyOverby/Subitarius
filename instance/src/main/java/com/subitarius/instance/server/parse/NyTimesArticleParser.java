/*
 * NyTimesArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.parse;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.subitarius.domain.Article;
import com.subitarius.domain.ArticleUrl;
import com.subitarius.domain.Team;
import com.subitarius.util.http.RobotsExclusionException;
import com.subitarius.util.http.SimpleHttpClient;

final class NyTimesArticleParser implements ArticleParser {
	private static enum PageType {
		ARTICLE {
			@Override
			String getTitle(Document document) {
				return document.select("meta[name=hdl]").attr("content");
			}

			@Override
			String getByline(Document document) {
				return document.select("meta[name=byl]").attr("content");
			}

			@Override
			Date getDate(Document document) throws ParseException {
				String dateStr = document.select("meta[name=pdate]").attr(
						"content");
				return DATE_FORMAT.parse(dateStr);
			}

			@Override
			List<Element> getElements(Document document) {
				return document.select("div.articleBody p");
			}
		},

		SCHOOLBOOK {
			@Override
			String getTitle(Document document) {
				return document.select("h1.sbook-headline").first().text();
			}

			@Override
			String getByline(Document document) {
				return document.select("p.sbook-bylines").first().text();
			}

			@Override
			Date getDate(Document document) throws ParseException {
				String dateStr = document.select("p.sbook-bydate").first()
						.text();
				return DATE_FORMAT.parse(dateStr);
			}

			@Override
			List<Element> getElements(Document document) {
				return document.select("div.sbook-post-content");
			}
		};

		abstract String getTitle(Document document);

		abstract String getByline(Document document);

		abstract Date getDate(Document document) throws ParseException;

		abstract List<Element> getElements(Document document);
	}

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");

	private final Provider<Team> teamProvider;

	private final SimpleHttpClient httpClient;

	@Inject
	private NyTimesArticleParser(Provider<Team> teamProvider,
			SimpleHttpClient httpClient) {
		this.teamProvider = teamProvider;
		this.httpClient = httpClient;
	}

	@Override
	public Article parse(ArticleUrl articleUrl) throws ArticleParseException {
		try {
			Document document = getDocument(articleUrl.getUrl());

			PageType pageType = null;
			String pageTypeStr = document.select("meta[name=PT]").attr(
					"content");
			if (pageTypeStr.equals("Article")) {
				pageType = PageType.ARTICLE;
			} else if (pageTypeStr.equals("Blogs")) {
				String blogName = document.select("meta[name=BN]").attr(
						"content");
				if (blogName.equals("schoolbook")) {
					pageType = PageType.SCHOOLBOOK;
				}
			}
			if (pageType == null) {
				throw new ArticleParseException(articleUrl);
			}

			String title = pageType.getTitle(document);
			String byline = pageType.getByline(document);
			Date date;
			try {
				date = pageType.getDate(document);
			} catch (ParseException px) {
				throw new ArticleParseException(articleUrl, px);
			}

			List<String> paragraphs = Lists.newArrayList();
			List<Element> elements = pageType.getElements(document);
			for (Element elem : elements) {
				String text = elem.text().trim();
				// NY Times uses U+0095 as a dot, which MySQL doesn't like
				text = text.replace('\u0095', '\u2022');
				if (!text.isEmpty()) {
					paragraphs.add(text);
				}
			}

			return new Article(teamProvider.get(), articleUrl, title, byline,
					date, paragraphs);
		} catch (IOException iox) {
			throw new ArticleParseException(articleUrl, iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(articleUrl, rex);
		}
	}

	private Document getDocument(String url) throws IOException,
			RobotsExclusionException {
		Map<String, String> params = ImmutableMap.of("pagewanted", "all");
		InputStream stream = httpClient.doGet(url, params);
		Document document = Jsoup.parse(stream, null, url);

		// they are evil and sometimes give us an ad page
		// fortunately, the ads have a meta tag that gives us a URL
		String urlContent = document.select("meta[http-equiv=refresh]").attr(
				"content");
		if (!urlContent.isEmpty()) {
			String urlFragment = urlContent.split(";")[1];
			return getDocument("http://www.nytimes.com" + urlFragment);
		} else {
			return document;
		}
	}
}
