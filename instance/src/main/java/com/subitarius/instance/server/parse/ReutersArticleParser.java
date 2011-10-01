/*
 * ReutersArticleParser.java
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.subitarius.domain.Article;
import com.subitarius.domain.ArticleUrl;
import com.subitarius.domain.Team;
import com.subitarius.util.http.RobotsExclusionException;
import com.subitarius.util.http.SimpleHttpClient;

final class ReutersArticleParser implements ArticleParser {
	/**
	 * The canonical URL for a slideshow feature.
	 */
	private static final String SLIDESHOW_URL = "http://www.reuters.com/article/slideshow";

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"EEE MMM d, yyyy");

	private final Provider<Team> teamProvider;

	private final SimpleHttpClient httpClient;

	@Inject
	private ReutersArticleParser(Provider<Team> teamProvider,
			SimpleHttpClient httpClient) {
		this.teamProvider = teamProvider;
		this.httpClient = httpClient;
	}

	@Override
	public Article parse(ArticleUrl articleUrl) throws ArticleParseException {
		String url = articleUrl.getUrl();
		if (url.equals(SLIDESHOW_URL)) {
			// slideshow, we can't parse this
			return null;
		}

		try {
			InputStream stream = httpClient.doGet(url);
			Document document = Jsoup.parse(stream, null, url);
			Element container = document.select("div.column2").first();
			// remove user comments
			container.select("div.articleComments").remove();

			String title = container.select("h1").first().text();

			Element bylineElem = container.select("p.byline").first();
			String byline = ((bylineElem == null) ? null : bylineElem.text());

			Date date;
			try {
				String dateStr = container.select("span.timestamp").first()
						.text();
				date = DATE_FORMAT.parse(dateStr);
			} catch (ParseException px) {
				throw new ArticleParseException(articleUrl, px);
			}

			List<String> paragraphs = Lists.newArrayList();
			List<Element> elements = container
					.select("span#articleText p, span#articleText pre");
			for (Element elem : elements) {
				// don't put the timestamp in the body
				if (elem.parent().attr("id").equals("articleInfo")) {
					continue;
				}
				String text = elem.text().replace("\n", " ").trim();
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
}
