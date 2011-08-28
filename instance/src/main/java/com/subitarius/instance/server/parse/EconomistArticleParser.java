/*
 * EconomistArticleParser.java
 * Copyright (C) 2011 Meyer Kizner, Ty Overby
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

final class EconomistArticleParser implements ArticleParser {
	private static enum ArticleType {
		BLOG("p.ec-blog-info", "div.ec-blog-body p") {
			@Override
			String getTitle(Document document) {
				return document.select("h1.ec-blog-headline").first().text();
			}

			@Override
			String getByline(Document document) {
				String blogInfo = document.select("p.ec-blog-info").first()
						.text();
				Matcher matcher = BYLINE_PATTERN.matcher(blogInfo);
				if (matcher.find()) {
					return matcher.group(1);
				} else {
					return null;
				}
			}
		},

		PRINT("p.ec-article-info", "div.ec-article-content p") {
			@Override
			String getTitle(Document document) {
				String title = document.select("div.headline").first().text();
				Element subtitle = document.select("h1.rubric").first();
				if (subtitle != null) {
					title += ": " + subtitle.text();
				}
				return title;
			}

			@Override
			String getByline(Document document) {
				return null;
			}
		};

		private final String dateSelector;

		private final String bodySelector;

		private ArticleType(String dateSelector, String bodySelector) {
			this.dateSelector = dateSelector;
			this.bodySelector = bodySelector;
		}

		abstract String getTitle(Document document);

		abstract String getByline(Document document);
	}

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"MMM dd yyyy");

	private static final Pattern BYLINE_PATTERN = Pattern
			.compile("(by (.+?))( \\||$)");

	private final Provider<Team> teamProvider;

	private final SimpleHttpClient httpClient;

	@Inject
	private EconomistArticleParser(Provider<Team> teamProvider,
			SimpleHttpClient httpClient) {
		this.teamProvider = teamProvider;
		this.httpClient = httpClient;
	}

	@Override
	public Article parse(ArticleUrl articleUrl) throws ArticleParseException {
		try {
			String url = articleUrl.getUrl();
			InputStream stream = httpClient.doGet(url);
			Document document = Jsoup.parse(stream, null, url);

			ArticleType type;
			if (document.body().className().contains("blog")) {
				type = ArticleType.BLOG;
			} else {
				type = ArticleType.PRINT;
			}

			String title = type.getTitle(document);
			String byline = type.getByline(document);
			Date date;
			try {
				Element dateElem = document.select(type.dateSelector).first();
				String dateStr = dateElem.text().replace("th", "")
						.replace("st", "").replace("rd", "").replace("nd", "");
				date = DATE_FORMAT.parse(dateStr);
			} catch (ParseException px) {
				throw new ArticleParseException(articleUrl, px);
			}

			List<String> paragraphs = Lists.newArrayList();
			List<Element> elements = document.select(type.bodySelector);
			for (Element elem : elements) {
				String text = elem.text().trim();
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
