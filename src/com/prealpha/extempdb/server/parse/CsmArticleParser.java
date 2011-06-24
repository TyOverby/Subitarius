/*
 * CsmArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import static com.google.common.base.Preconditions.*;

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
import com.prealpha.extempdb.server.http.HttpClient;
import com.prealpha.extempdb.server.http.RobotsExclusionException;

final class CsmArticleParser extends AbstractArticleParser {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"MMMMM d, yyyy");

	private final HttpClient httpClient;

	@Inject
	private CsmArticleParser(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public String getCanonicalUrl(String url) {
		url = super.getCanonicalUrl(url);
		if (url.matches(".*/\\(page\\)/\\d+")) {
			int index = url.lastIndexOf("/(page)");
			return url.substring(0, index);
		} else {
			return url;
		}
	}

	@Override
	public ProtoArticle parse(String url) throws ArticleParseException {
		checkNotNull(url);

		Map<String, String> params = Collections.emptyMap();

		try {
			List<Document> documents = Lists.newArrayList();
			Document document;
			int page = 1;
			String pageUrl = url;
			do {
				InputStream stream = httpClient.doGet(pageUrl, params);
				document = Jsoup.parse(stream, null, pageUrl);
				if (isParseable(document)) {
					documents.add(document);
					pageUrl = url + "/(page)/" + (++page);
				} else {
					return null;
				}
			} while (!document.select("a#next-button").isEmpty());

			return getFromDocuments(documents);
		} catch (IOException iox) {
			throw new ArticleParseException(iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(rex);
		}
	}

	private static boolean isParseable(Document document) {
		// check for lists and quizzes
		Element content = document.select("div.list-article-full, div.ui-quiz")
				.first();
		return (content == null);
	}

	private static ProtoArticle getFromDocuments(List<Document> documents)
			throws ArticleParseException {
		checkArgument(!documents.isEmpty());
		String title = documents.get(0).select("h1.head").first().text();
		String[] metaStr = documents.get(0).select("p.sByline").first().text()
				.split("/");

		String byline = metaStr[0];
		if (byline.contains(",")) {
			byline = byline.substring(0, byline.indexOf(','));
		}

		String dateStr = metaStr[metaStr.length - 1].trim();
		Date date;
		try {
			date = DATE_FORMAT.parse(dateStr);
		} catch (ParseException px) {
			throw new ArticleParseException(px);
		}

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

		return new ProtoArticle(title, byline, date, paragraphs);
	}
}
