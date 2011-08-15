/*
 * CsmArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import static com.google.common.base.Preconditions.*;

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

final class CsmArticleParser implements ArticleParser {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"MMMMM d, yyyy");

	private final Provider<Team> teamProvider;

	private final SimpleHttpClient httpClient;

	@Inject
	private CsmArticleParser(Provider<Team> teamProvider,
			SimpleHttpClient httpClient) {
		this.teamProvider = teamProvider;
		this.httpClient = httpClient;
	}

	@Override
	public Article parse(ArticleUrl articleUrl) throws ArticleParseException {
		String url = articleUrl.getUrl();
		try {
			List<Document> documents = Lists.newArrayList();
			Document document;
			int page = 1;
			String pageUrl = url;
			do {
				InputStream stream = httpClient.doGet(pageUrl);
				document = Jsoup.parse(stream, null, pageUrl);
				if (isParseable(document)) {
					documents.add(document);
					pageUrl = url + "/(page)/" + (++page);
				} else {
					return null;
				}
			} while (!document.select("a#next-button").isEmpty());

			return getFromDocuments(articleUrl, documents);
		} catch (ParseException px) {
			throw new ArticleParseException(articleUrl, px);
		} catch (IOException iox) {
			throw new ArticleParseException(articleUrl, iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(articleUrl, rex);
		}
	}

	private static boolean isParseable(Document document) {
		// check for lists and quizzes
		Element content = document.select("div.list-article-full, div.ui-quiz")
				.first();
		return (content == null);
	}

	private Article getFromDocuments(ArticleUrl articleUrl,
			List<Document> documents) throws ParseException {
		checkArgument(!documents.isEmpty());
		String title = documents.get(0).select("h1.head").first().text();
		String[] metaStr = documents.get(0).select("p.sByline").first().text()
				.split("/");

		String byline = metaStr[0];
		if (byline.contains(",")) {
			byline = byline.substring(0, byline.indexOf(','));
		}

		String dateStr = metaStr[metaStr.length - 1].trim();
		Date date = DATE_FORMAT.parse(dateStr);

		List<String> paragraphs = Lists.newArrayList();
		for (Document document : documents) {
			List<Element> elements = document.select("div.sBody > p");
			for (Element elem : elements) {
				String text = elem.text().trim();
				if (!text.isEmpty()) {
					paragraphs.add(text);
				}
			}
		}

		return new Article(teamProvider.get(), articleUrl, title, byline, date,
				paragraphs);
	}
}
