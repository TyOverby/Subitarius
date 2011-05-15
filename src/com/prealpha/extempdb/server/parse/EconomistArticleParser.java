/*
 * EconomistArticleParser.java
 * Copyright (C) 2011 Meyer Kizner, Ty Overby
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;

import com.google.inject.Inject;
import com.prealpha.extempdb.server.http.HttpClient;
import com.prealpha.extempdb.server.http.RobotsExclusionException;

final class EconomistArticleParser extends AbstractArticleParser {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"MMM dd yyyy");

	private static final Pattern BYLINE_PATTERN = Pattern
			.compile("(by (.+?))( \\||$)");

	private final HttpClient httpClient;

	@Inject
	public EconomistArticleParser(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public ProtoArticle parse(String url) throws ArticleParseException {
		checkNotNull(url);

		// we need the printable version
		url += "/print";

		try {
			Map<String, String> params = Collections.emptyMap();
			InputStream stream = httpClient.doGet(url, params);
			return getFromHtml(stream, url.contains("blogs"));
		} catch (IOException iox) {
			throw new ArticleParseException(iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(rex);
		}
	}

	private ProtoArticle getFromHtml(InputStream html, boolean blog)
			throws ArticleParseException {
		Document document = ParseUtils.parse(html);

		// get the title
		String title;
		if (blog) {
			Element titleElement = ParseUtils.searchDescendants(document, "h1",
					"class", "ec-blog-headline").get(0);
			title = titleElement.getValue();
		} else {
			Element titleElement = ParseUtils.searchDescendants(document,
					"div", "class", "headline").get(0);
			List<Element> subTitles = ParseUtils.searchDescendants(document,
					"h1", "class", "rubric");

			// handle articles with and without subtitles
			if (!subTitles.isEmpty()) {
				Element subTitleElement = ParseUtils.searchDescendants(
						document, "h1", "class", "rubric").get(0);
				title = titleElement.getValue() + ": "
						+ subTitleElement.getValue();
			} else {
				title = titleElement.getValue();
			}
		}

		// get the byline
		String byline = null;
		if (blog) {
			List<Element> blogInfo = ParseUtils.searchDescendants(document,
					"p", "class", "ec-blog-info");
			if (!blogInfo.isEmpty()) {
				String blogInfoStr = blogInfo.get(0).getValue();
				Matcher matcher = BYLINE_PATTERN.matcher(blogInfoStr);
				if (matcher.find()) {
					byline = matcher.group(1);
				}
			}
		}

		// get the date
		Date date;
		try {
			Element dateElement;
			if (blog) {
				dateElement = ParseUtils.searchDescendants(document, "p",
						"class", "ec-blog-info").get(0);

			} else {
				dateElement = ParseUtils.searchDescendants(document, "p",
						"class", "ec-article-info").get(0);
			}

			String dateStr = dateElement.getText().replace("th", "")
					.replace("st", "").replace("rd", "").replace("nd", "");
			date = DATE_FORMAT.parse(dateStr);
		} catch (ParseException px) {
			throw new ArticleParseException(px);
		}

		// get the body text
		Filter bodyElementFilter;
		if (blog) {
			bodyElementFilter = ParseUtils.getElementFilter("div", "class",
					"ec-blog-body");

		} else {
			bodyElementFilter = ParseUtils.getElementFilter("div", "class",
					"ec-article-content clear");
		}
		Iterator<?> i1 = document.getDescendants(bodyElementFilter);
		List<String> paragraphs = new ArrayList<String>();
		while (i1.hasNext()) {
			Element bodyElement = (Element) i1.next();

			Filter paragraphFilter = ParseUtils.getElementFilter("p", null,
					null);
			Iterator<?> i2 = bodyElement.getDescendants(paragraphFilter);
			while (i2.hasNext()) {
				Element paragraph = (Element) i2.next();
				String text = paragraph.getValue();
				if (!text.isEmpty()) {
					paragraphs.add(text);
				}
			}
		}

		return new ProtoArticle(title, byline, date, paragraphs);
	}
}
