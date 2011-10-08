/*
 * AlJazeeraArticleParser.java
 * Copyright (C) 2011 Ty Overby, Meyer Kizner
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

final class AlJazeeraArticleParser implements ArticleParser {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd MMM yyyy");

	private final Provider<Team> teamProvider;

	private final SimpleHttpClient httpClient;

	@Inject
	private AlJazeeraArticleParser(Provider<Team> teamProvider,
			SimpleHttpClient httpClient) {
		this.teamProvider = teamProvider;
		this.httpClient = httpClient;
	}

	@Override
	public Article parse(ArticleUrl articleUrl) throws ArticleParseException {
		try {
			String url = articleUrl.getUrl();
			InputStream stream = httpClient.doGet(url + "?print=true");
			Document document = Jsoup.parse(stream, null, url);

			String title = document.select("span#DetailedTitle").text();

			String byline;
			if (!document.select("span.byLine").isEmpty()) {
				byline = document.select("span.byLine").text();
			} else {
				byline = null;
			}

			String dateStr = document.select("span#dvArticleDate").text()
					.replace("Last Modified: ", "");
			Date date;
			try {
				date = DATE_FORMAT.parse(dateStr);
			} catch (ParseException px) {
				throw new ArticleParseException(articleUrl, px);
			}

			List<String> paragraphs = Lists.newArrayList();
			for (Element element : document
					.select("#tdTextContent > p, #tdTextContent > div:not(:has(p))")) {
				String text = element.text().replace('\u00a0', ' ').trim();
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
