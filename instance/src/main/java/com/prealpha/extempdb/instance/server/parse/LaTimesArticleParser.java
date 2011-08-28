/*
 * LaTimesArticleParser.java
 * Copyright (C) 2011 Ty Overby
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

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
import com.prealpha.extempdb.domain.Article;
import com.prealpha.extempdb.domain.ArticleUrl;
import com.prealpha.extempdb.domain.Team;
import com.prealpha.extempdb.util.http.RobotsExclusionException;
import com.prealpha.extempdb.util.http.SimpleHttpClient;

public class LaTimesArticleParser implements ArticleParser {
	private static final DateFormat DATE_FORMAT_BLOG = new SimpleDateFormat(
			"MMMMM dd, yyyy");
	private final Provider<Team> teamProvider;
	private final SimpleHttpClient httpClient;

	@Inject
	private LaTimesArticleParser(Provider<Team> teamProvider,
			SimpleHttpClient httpClient) {
		this.teamProvider = teamProvider;
		this.httpClient = httpClient;
	}

	@Override
	public Article parse(ArticleUrl articleUrl) throws ArticleParseException {
		if (articleUrl.getUrl().contains("latimesblogs")) {
			return parseBlog(articleUrl);
		} else {
			return parseArticle(articleUrl);
		}
	}

	private Article parseArticle(ArticleUrl articleUrl)
			throws ArticleParseException {
		try {
			String title;
			Date date;
			String author = null;
			List<String> paragraphs = Lists.newArrayList();

			// setup
			String url = articleUrl.getUrl();
			InputStream stream = httpClient.doGet(url);
			Document document = Jsoup.parse(stream, null, url);

			return null;
		} catch (IOException iox) {
			throw new ArticleParseException(articleUrl, iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(articleUrl, rex);
		}
	}

	private Article parseBlog(ArticleUrl articleUrl)
			throws ArticleParseException {
		try {
			String title;
			Date date;
			String author = null;
			List<String> paragraphs = Lists.newArrayList();

			// setup
			String url = articleUrl.getUrl();
			InputStream stream = httpClient.doGet(url);
			Document document = Jsoup.parse(stream, null, url);
			document.outputSettings().charset("ASCII");

			// title
			title = document.select("h1.entry-header").text();

			// date
			String dateStr = document.select("div.time").first().text();
			try {
				date = DATE_FORMAT_BLOG.parse(dateStr);
			} catch (ParseException px) {
				throw new ArticleParseException(articleUrl, px);
			}

			// paragraphs
			boolean contentOn = true;
			for (Element elem : document.select("div.entry-body p")) {

				if (elem.text().contains("RELATED")) {
					contentOn = false;
					System.out.println("OFF");
				}
				if (contentOn) {
					if (elem.text().trim().length() > 0) {
						paragraphs.add(elem.text());
					}
				}

				if (elem.text().startsWith("--")) {
					author = elem.text().replace("-- ", "");
				}
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
