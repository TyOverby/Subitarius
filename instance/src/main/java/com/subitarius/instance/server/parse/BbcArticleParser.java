/*
 * BbcArticleParser.java
 * Copyright (C) 2011 Ty Overby
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

final class BbcArticleParser implements ArticleParser {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd MMMM yyyy");

	private final Provider<Team> teamProvider;

	private final SimpleHttpClient httpClient;

	@Inject
	private BbcArticleParser(Provider<Team> teamProvider,
			SimpleHttpClient httpClient) {
		this.teamProvider = teamProvider;
		this.httpClient = httpClient;
	}

	@Override
	public Article parse(ArticleUrl articleUrl) throws ArticleParseException {
		try {
			String title;
			Date date;
			String author = null;
			List<String> paragraphs = Lists.newArrayList();

			// setup
			// canonical URL will never contain parameters
			String url = articleUrl.getUrl();
			InputStream stream = httpClient.doGet(url + "?print=true");
			Document document = Jsoup.parse(stream, null, url);

			// drop some content that can mess up our text
			document.select(".videoInStoryA, .videoInStoryB, .videoInStoryC")
					.remove();

			// title
			title = document.select("h1.story-header").first().text();

			// date
			String dateStr = document.select("span.date").first().text();
			try {
				date = DATE_FORMAT.parse(dateStr);
			} catch (ParseException px) {
				throw new ArticleParseException(articleUrl, px);
			}

			// paragraphs
			for (Element elem : document.select("div.story-body p")) {
				paragraphs.add(elem.text());
			}

			return new Article(teamProvider.get(), articleUrl, title, author,
					date, paragraphs);
		} catch (IOException iox) {
			throw new ArticleParseException(articleUrl, iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(articleUrl, rex);
		}
	}
}
